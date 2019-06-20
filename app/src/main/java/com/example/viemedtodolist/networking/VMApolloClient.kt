package com.example.viemedtodolist.networking

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.viemedtodolist.*
import com.example.viemedtodolist.data.Task
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Apollo Client that handles the graphql api calls.
 *
 * DISCLAIMER
 * I've hardcoded the access token for the following reason: the endpoint provided does not respond with the correct status
 * code (401) upon an unauthorized request. This means that I can't create an Okhttp3.Authenticator to do a proper authentication
 * because the authenticator is triggered with that request code.
 *
 * I thought about implementing an Interceptor that makes the api call and set the access token but to my surprise the Apollo Client
 * doesn't provide a method for synchronous request (don't know why) which are required for this approach.
 *
 * The last option that comes to mind is to create a client, generate the access token and then replace the client instance with
 * another that has an Interceptor with the access token.
 *
 * Maybe I'm missing something as this is my first time with a graphql endpoint and client.
 */
object VMApolloClient {

    private const val BASE_URL = "https://380odjc5vi.execute-api.us-east-1.amazonaws.com/dev/graphql"
    private const val ACCESS_TOKEN =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImFtb3VuaWVyIiwidXNlcklkIjoiMzY2MWE2NWUtZDUyNS01ZTMwLTgwYjctODBhZjJmNjNmMWNmIiwidGltZXN0YW1wIjoxNTYwNzgwODg2MTU3LCJhcGlLZXkiOiI1NzA4Mzc1Ny0yNDc2LTRmNWUtOGQyYi02NmE5ZDA3ZmRiMjEiLCJjb250cm9sIjoiYTMwNTIwMjYtZmQwNC01NTMxLTlmYjktMmYyYmM2YzUyNDQ3IiwiaWF0IjoxNTYwNzgwODg2fQ.9r0urAGHs6y_uUAlNT-IEGshWmkNCpA2f4XOVjTmgg8"

    private val apolloClient: ApolloClient

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

    fun getAllTasks(callback: (List<Task>?, ApolloException?) -> Unit) {
        apolloClient.query(AllTasksQuery.builder().build()).enqueue(
            object : ApolloCall.Callback<AllTasksQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(null, e)

                }

                override fun onResponse(response: Response<AllTasksQuery.Data>) {
                    val data = response.data() as AllTasksQuery.Data
                    val taskList = transformToTaskList(data.allTasks()!!)
                    callback(taskList, null)
                }

            }
        )
    }

    fun createTask(name: String, note: String, isDone: Boolean, callback: (Task?, ApolloException?) -> Unit) {
        val createTaskMutation = CreateTaskMutation.builder().name(name).note(note).isDone(isDone).build()
        apolloClient.mutate(createTaskMutation).enqueue(object : ApolloCall.Callback<CreateTaskMutation.Data>() {
            override fun onFailure(e: ApolloException) {
                callback(null, e)
            }

            override fun onResponse(response: Response<CreateTaskMutation.Data>) {
                callback(transformToTask(response.data()!!.createTask()!!), null)
            }
        })
    }

    fun deleteTask(id: String, callback: (Boolean?, ApolloException?) -> Unit) {
        val deleteTaskMutation = DeleteTaskMutation.builder().id(id).build()
        apolloClient.mutate(deleteTaskMutation).enqueue(object : ApolloCall.Callback<DeleteTaskMutation.Data>() {
            override fun onFailure(e: ApolloException) {
                callback(null, e)
            }

            override fun onResponse(response: Response<DeleteTaskMutation.Data>) {
                callback(response.data()!!.deleteTask(), null)
            }

        })
    }

    fun updateTaskStatus(id: String, isDone: Boolean, callback: (Boolean?, ApolloException?) -> Unit) {
        val updateTaskStatusMutation = UpdateTaskStatusMutation.builder().id(id).isDone(isDone).build()
        apolloClient.mutate(updateTaskStatusMutation).enqueue(
            object : ApolloCall.Callback<UpdateTaskStatusMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(null, e)
                }

                override fun onResponse(response: Response<UpdateTaskStatusMutation.Data>) {
                    callback(response.data()?.updateTaskStatus()?.isDone, null)
                }
            })
    }

    class AccessTokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val original = chain.request()
            val builder = original.newBuilder().method(original.method(), original.body())
            builder.header("Authorization", ACCESS_TOKEN)
            return chain.proceed(builder.build())
        }
    }

    /**
     * Here is were I fail :(
     * I couldn't configure apollo to return the same DTO so I need to transform it to store it.
     */
    private fun transformToTaskList(allTaskList: MutableList<AllTasksQuery.AllTask>): List<Task> {
        val taskList = mutableListOf<Task>()
        allTaskList.forEach { taskList.add(Task(it.id(), it.name(), it.note(), it.isDone)) }
        return taskList
    }

    private fun transformToTask(createdTask: CreateTaskMutation.CreateTask): Task {
        return Task(createdTask.id(), createdTask.name(), createdTask.note(), createdTask.isDone)
    }
}