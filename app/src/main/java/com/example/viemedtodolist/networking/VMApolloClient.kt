package com.example.viemedtodolist.networking

import com.apollographql.apollo.ApolloCall.Callback
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.viemedtodolist.*
import com.example.viemedtodolist.data.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Apollo Client that handles the graphql api calls.
 *
 * DISCLAIMER
 * The endpoint provided does not respond with the correct status code (401) upon an unauthorized request.
 * This means that I can't create an Okhttp3.Authenticator to do a proper authentication because the authenticator is
 * triggered with that request code.
 *
 * I thought about implementing an Interceptor that makes the api call and set the access token but to my surprise the Apollo Client
 * doesn't provide a method for synchronous request (don't know why) which are required for this approach. This is why I had to
 * implement a synchronous request for the access token with coroutines.
 */
object VMApolloClient : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private const val BASE_URL = "https://380odjc5vi.execute-api.us-east-1.amazonaws.com/dev/graphql"
    private const val USER_NAME = "amounier"
    private const val API_KEY = "57083757-2476-4f5e-8d2b-66a9d07fdb21"

    private val apolloClient: ApolloClient
    private var accessToken: String? = null
    private var fetchingAccessToken = false


    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AccessTokenInterceptor())
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        apolloClient = ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient).build()
    }

    fun getAllTasks(callback: Callback<AllTasksQuery.Data>) {
        apolloClient.query(AllTasksQuery.builder().build()).enqueue(callback)
    }

    fun createTask(name: String, note: String, isDone: Boolean, callback: Callback<CreateTaskMutation.Data>) {
        val createTaskMutation = CreateTaskMutation.builder().name(name).note(note).isDone(isDone).build()
        apolloClient.mutate(createTaskMutation).enqueue(callback)
    }

    fun deleteTask(id: String, callback: Callback<DeleteTaskMutation.Data>) {
        val deleteTaskMutation = DeleteTaskMutation.builder().id(id).build()
        apolloClient.mutate(deleteTaskMutation).enqueue(callback)
    }

    fun updateTaskStatus(id: String, isDone: Boolean, callback: Callback<UpdateTaskStatusMutation.Data>) {
        val updateTaskStatusMutation = UpdateTaskStatusMutation.builder().id(id).isDone(isDone).build()
        apolloClient.mutate(updateTaskStatusMutation).enqueue(callback)
    }

    private fun generateAccessToken() {
        if (accessToken.isNullOrEmpty()) {
            if (!fetchingAccessToken) {
                runBlocking {
                    accessToken = getAccessToken()
                    fetchingAccessToken = false
                }
            }
        }
    }

    private suspend fun getAccessToken(): String? = suspendCoroutine { cont ->
        fetchingAccessToken = true
        val accessTokenMutation = GenerateAccessTokenMutation.builder().apiKey(API_KEY).userName(USER_NAME).build()
        apolloClient.mutate(accessTokenMutation).enqueue(
            object : Callback<GenerateAccessTokenMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    cont.resume(null)
                }

                override fun onResponse(response: Response<GenerateAccessTokenMutation.Data>) {
                    cont.resume(response.data()?.generateAccessToken())
                }
            }
        )
    }


    private class AccessTokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val original = chain.request()
            val builder = original.newBuilder().method(original.method(), original.body())
            generateAccessToken()
            if (!accessToken.isNullOrEmpty()) {
                builder.header("Authorization", accessToken!!)
            }
            return chain.proceed(builder.build())
        }
    }

    /**
     * Apollo should have the option to set the return type of the data, sadly this is not an option.
     * The transformation IMO should be the clients responsibility so that's why this functions are here.
     */
    fun transformToTaskList(allTaskList: List<AllTasksQuery.AllTask>) = allTaskList.map {
        Task(it.id(), it.name(), it.note(), it.isDone)
    }

    fun transformToTask(createdTask: CreateTaskMutation.CreateTask): Task {
        return Task(createdTask.id(), createdTask.name(), createdTask.note(), createdTask.isDone)
    }
}