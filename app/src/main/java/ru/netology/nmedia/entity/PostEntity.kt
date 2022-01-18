package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val viewed: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val ownedByMe: Boolean = false
) {
    fun toDto() = Post(
        id,
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
        viewed,
        ownedByMe
    )

    companion object {
        fun fromDto(post: Post) =
            PostEntity(
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
                AttachmentEmbeddable.fromDto(post.attachment),
                post.ownedByMe
            )
    }
}

fun List<PostEntity>.toDto() = map(PostEntity::toDto)
fun List<Post>.toEntity() = map(PostEntity::fromDto)