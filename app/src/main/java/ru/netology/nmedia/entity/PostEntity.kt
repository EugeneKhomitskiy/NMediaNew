package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val viewed: Boolean = true
) {

    fun toDto() = Post(
        id,
        authorAvatar,
        author,
        content,
        published,
        likedByMe,
        likes,
        shares,
        views,
        attachments = null,
        viewed
    )

    companion object {
        fun fromDto(post: Post) =
            PostEntity(
                post.id,
                post.authorAvatar,
                post.author,
                post.content,
                post.published,
                post.likedByMe,
                post.likes,
                post.views,
                post.shares,
                post.viewed
            )
    }
}

fun List<PostEntity>.toDto() = map(PostEntity::toDto)
fun List<Post>.toEntity() = map(PostEntity::fromDto)