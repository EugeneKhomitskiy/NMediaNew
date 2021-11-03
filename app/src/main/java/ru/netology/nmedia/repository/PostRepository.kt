package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data : Flow<List<Post>>

    suspend fun getAllAsync()
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun likeByIdAsync(id: Long)
    suspend fun unlikeByIdAsync(id: Long)

    fun getNewerCount(id: Long): Flow<Int>
}
