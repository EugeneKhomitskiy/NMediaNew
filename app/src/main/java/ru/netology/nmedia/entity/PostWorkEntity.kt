package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val viewed: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    var uri: String? = null,
) {
    fun toDto() = Post(
        postId,
        authorId,
        authorAvatar,
        author,
        content,
        published,
        likedByMe,
        likes,
        shares,
        views,
        attachment?.toDto(),
        viewed
    )

    companion object {
        fun fromDto(post: Post) =
            PostWorkEntity(
                0L,
                post.id,
                post.authorId,
                post.authorAvatar,
                post.author,
                post.content,
                post.published,
                post.likedByMe,
                post.likes,
                post.views,
                post.shares,
                post.viewed,
                AttachmentEmbeddable.fromDto(post.attachment)
            )
    }
}