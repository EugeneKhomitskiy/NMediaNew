package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://192.168.0.103:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: error("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.GetPostCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(id).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                }

            })
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.GetPostCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                }

            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetPostCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(post)
                }

            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemoveCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(id)
                }

            })
    }

    override fun shareByIdAsync(id: Long, callback: PostRepository.GetPostCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(id).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/shares")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                }

            })
    }

    override fun viewsByIdAsync(id: Long, callback: PostRepository.GetPostCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(id).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/views")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                }

            })
    }
}
