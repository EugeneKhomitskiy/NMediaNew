package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity.Companion.fromDto
import ru.netology.nmedia.enumeration.AttachmentType

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
    val viewed: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable?
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
        attachment?.toDto()
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
                post.viewed,
                AttachmentEmbeddable.fromDto(post.attachment)
            )
    }
}

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

fun List<PostEntity>.toDto() = map(PostEntity::toDto)
fun List<Post>.toEntity() = map(PostEntity::fromDto)