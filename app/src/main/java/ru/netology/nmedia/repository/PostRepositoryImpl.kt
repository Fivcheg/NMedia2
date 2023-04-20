package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.lang.RuntimeException

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient
        .Builder()
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        const val BASE_URL_OPEN = BASE_URL
        private val jsonType = "application/json".toMediaType()
        val jsonTypeOpen = jsonType
    }

    override fun getAll(callback: PostRepository.PostsCallback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(
                call: retrofit2.Call<List<Post>>,
                response: retrofit2.Response<List<Post>>
            ) {
                if (response.code()/100 == 2) {
                    callback.onSuccess(requireNotNull(response.body()) { "body is null" })
                } else {
                    callback.onError(RuntimeException(response.message()))
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun likeById(
        id: Long,
        likedByMe: Boolean,
        callback: PostRepository.PostsCallback<Post>
    ) {
        PostsApi.retrofitService
            .run { if (!likedByMe) likeById(id) else dislikeById(id) }
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (response.code()/100 == 2) {
                        callback.onSuccess(requireNotNull(response.body()) { "body is null" })
                    } else {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun save(post: Post?, callback: PostRepository.PostsCallback<Post>) {
        PostsApi.retrofitService.save(requireNotNull(post))
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (response.code()/100 == 2) {
                        callback.onSuccess(requireNotNull(response.body()) { "body is null" })
                    } else {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.PostsCallback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (response.code()/100 == 2) {
                    callback.onSuccess(requireNotNull(response.body()) { "body is null" })
                } else {
                    callback.onError(RuntimeException(response.message()))
                }
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

}



