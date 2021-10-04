package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0
)

