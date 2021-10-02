package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAllAsync(callback: GetAllCallback)
    fun removeByIdAsync(id: Long, callback: RemoveCallback)
    fun saveAsync(post: Post, callback: GetPostCallback)
    fun likeByIdAsync(id: Long, callback: GetPostCallback)
    fun unlikeByIdAsync(id: Long, callback: GetPostCallback)
    fun shareByIdAsync(id: Long, callback: GetPostCallback)
    fun viewsByIdAsync(id: Long, callback: GetPostCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }

    interface RemoveCallback {
        fun onSuccess(id: Long)
        fun onError(e: Exception)
    }

    interface GetPostCallback {
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }
}
