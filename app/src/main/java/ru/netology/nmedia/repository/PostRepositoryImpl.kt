package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException
import java.lang.Exception

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll().map { it.toDto() }

    override suspend fun getAllAsync() {
        try {
            val response = PostApi.retrofitService.getAll()
            PostApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val data = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(data.toEntity())
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeByIdAsync(id: Long) {
        PostApi.retrofitService.removeById(id)
    }

    override suspend fun saveAsync(post: Post) {
        PostApi.retrofitService.save(post)
    }

    override suspend fun likeByIdAsync(id: Long) {
        PostApi.retrofitService.likeById(id)
    }

    override suspend fun unlikeByIdAsync(id: Long) {
        PostApi.retrofitService.unlikeById(id)
    }
}
