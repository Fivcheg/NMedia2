package ru.netology.nmedia.api

import android.telecom.Call
import com.google.android.datatransport.runtime.logging.Logging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post


private const val BASE_URL =  "${BuildConfig.BASE_URL}/api/slow/"

interface PostsApiService {
    @GET("posts")
    fun getAll(): retrofit2.Call<List<Post>>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long): retrofit2.Call<Post>

    @POST("posts")
    fun save(@Body post: Post): retrofit2.Call<Post>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Long): retrofit2.Call<Unit>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Long): retrofit2.Call<Post>

    @GET("posts/{id}")
    fun getById(@Path("id") id: Long): retrofit2.Call<Post>
}

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
private val client =  OkHttpClient.Builder().addInterceptor(logging).build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL).build()

object PostsApi {
    val retrofitService: PostsApiService by lazy {
        retrofit.create()
    }
}