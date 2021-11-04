package ru.netology.nmedia.error

import java.io.IOException
import java.sql.SQLException

sealed class AppError(message: String): Exception(message) {
    companion object {
        fun from(e: Throwable) = when(e) {
            is IOException -> NetworkError
            is SQLException -> DbError
            is ApiError -> e
            else -> UnknownError
        }
    }
}

class ApiError(message: String): AppError(message)

object NetworkError : AppError("network error")

object UnknownError : AppError("unknown error")

object DbError : AppError("db error")