package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException

class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            var counter = 5
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    if (counter > 0) {
                        PostApi.retrofitService.getAll().enqueue(this)
                        counter--
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else callback.onSuccess(response.body() ?: error("body is null"))
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
        PostApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            var counter = 5
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    if (counter > 0) {
                        PostApi.retrofitService.removeById(id).enqueue(this)
                        counter--
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else callback.onSuccess(Unit)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }

    override fun saveAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            var counter = 5
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    if (counter > 0) {
                        PostApi.retrofitService.save(post).enqueue(this)
                        counter--
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                }
                else callback.onSuccess(response.body() ?: error("body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            var counter = 5
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    if (counter > 0) {
                        PostApi.retrofitService.likeById(id).enqueue(this)
                        counter--
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else callback.onSuccess(response.body() ?: error("body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.unlikeById(id).enqueue(object : Callback<Post> {
            var counter = 5
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    if (counter > 0) {
                        PostApi.retrofitService.unlikeById(id).enqueue(this)
                        counter--
                    } else {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                } else callback.onSuccess(response.body() ?: error("body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }
}
