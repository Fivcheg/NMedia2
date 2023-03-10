package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient
        .Builder()
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: PostRepository.PostsCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    } else {
                        try {
                            callback.onSuccess(
                                gson.fromJson(
                                    requireNotNull(response.body?.string()) { "body is null" },
                                    typeToken
                                )
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            })
    }

    override fun likeById(
        id: Long,
        likedByMe: Boolean,
        callback: PostRepository.PostsCallback<Post>
    ) {
        val request: Request = Request.Builder()
            .run {
                if (likedByMe) {
                    delete()
                } else {
                    post(gson.toJson(id).toRequestBody(jsonType))
                }
            }
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    } else {
                        try {
                            callback.onSuccess(
                                gson.fromJson(
                                    requireNotNull(response.body?.string()) { "body is null" },
                                    Post::class.java
                                )
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            })
    }

    override fun save(post: Post) {

        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}
