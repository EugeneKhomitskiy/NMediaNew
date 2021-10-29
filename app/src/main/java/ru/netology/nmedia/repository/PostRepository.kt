package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data : LiveData<List<Post>>

    suspend fun getAllAsync()
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun likeByIdAsync(id: Long)
    suspend fun unlikeByIdAsync(id: Long)

}
