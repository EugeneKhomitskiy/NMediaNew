package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAllAsync(callback: GetAllCallback)
    fun saveAsync(post: Post, callback: SaveCallback)
    fun removeByIdAsync(id: Long, callback: GetByIdCallback)
    fun likeByIdAsync(id: Long, callback: GetByIdCallback)
    fun unlikeByIdAsync(id: Long, callback: GetByIdCallback)
    fun shareByIdAsync(id: Long, callback: GetByIdCallback)
    fun viewsByIdAsync(id: Long, callback: GetByIdCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }

    interface SaveCallback {
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }

    interface GetByIdCallback {
        fun onSuccess(id: Long)
        fun onError(e: Exception)
    }
}
