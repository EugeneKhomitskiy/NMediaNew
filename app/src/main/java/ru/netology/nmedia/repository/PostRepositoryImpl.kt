package ru.netology.nmedia.repository

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostWorkDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostWorkEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val workDao: PostWorkDao,
    private val apiService: ApiService
    ) :
    PostRepository {

    override val data: Flow<List<Post>> = dao.getAll()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun getAllAsync() {
        try {
            dao.getAll()
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            dao.insert(data.toEntity().map {
                it.copy(viewed = true)
            })
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun getNewPosts() {
        try {
            dao.setPostsViewed()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            dao.insert(data.toEntity().map {
                it.copy(viewed = false)
            })
            emit(data.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun removeByIdAsync(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveAsync(post: Post) {
        try {
            dao.save(PostEntity.fromDto(post))
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            dao.insert(PostEntity.fromDto(data).copy(viewed = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeByIdAsync(id: Long) {
        try {
            dao.likeById(id)
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            dao.insert(PostEntity.fromDto(data).copy(viewed = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun unlikeByIdAsync(id: Long) {
        try {
            dao.unlikeById(id)
            val response = apiService.unlikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            dao.insert(PostEntity.fromDto(data).copy(viewed = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, "", AttachmentType.IMAGE))
            saveAsync(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            return response.body() ?: throw ApiError(response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveWork(post: Post, upload: MediaUpload?): Long {
        try {
            val entity = PostWorkEntity.fromDto(post).apply {
                if (upload != null) {
                    this.uri = upload.file.toUri().toString()
                }
            }
            return workDao.insert(entity)
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun processWork(id: Long) {
        try {
            val entity = workDao.getById(id)
            if (entity.uri != null) {
                val upload = MediaUpload(Uri.parse(entity.uri).toFile())
                saveWithAttachment(entity.toDto(), upload)
            } else saveAsync(entity.toDto())
            workDao.removeById(id)
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}
