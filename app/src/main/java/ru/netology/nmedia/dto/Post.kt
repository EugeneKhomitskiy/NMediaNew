package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val attachments: Attachments?
)

data class Attachments(
    val url: String,
    val description: String,
    val type: AttachmentType
)


