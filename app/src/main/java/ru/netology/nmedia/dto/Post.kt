package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.PostTime

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val authorId: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: Long = 0,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val attachment: Attachment?,
    val viewed: Boolean = true,
    val ownedByMe: Boolean = false
) : FeedItem {
    companion object {
        val empty = Post(
            id = 0,
            authorId = 0,
            content = "",
            authorAvatar = "",
            author = "",
            likedByMe = false,
            likes = 0,
            published = 0,
            attachment = null
        )
    }
}

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem

data class Time(
    override val id: Long,
    val timeStr: PostTime,
) : FeedItem

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType
)


