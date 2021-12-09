package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data : Flow<List<Post>>

    suspend fun getAllAsync()
    suspend fun getNewPosts()
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun likeByIdAsync(id: Long)
    suspend fun unlikeByIdAsync(id: Long)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun saveWork(post: Post, upload: MediaUpload?): Long
    suspend fun processWork(id: Long)

    fun getNewerCount(id: Long): Flow<Int>
}
