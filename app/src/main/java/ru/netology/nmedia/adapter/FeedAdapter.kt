package ru.netology.nmedia.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardTimeBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Time
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.view.load
import java.time.Instant
import java.util.*

private const val BASE_URL = BuildConfig.BASE_URL

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onViews(post: Post) {}
    fun openImage(post: Post) {}
}

class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is Time -> R.layout.card_time
            null -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.card_time -> {
                val binding = CardTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TimeViewHolder(binding)
            }
            else -> error("unknown view type $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is Time -> (holder as? TimeViewHolder)?.bind(item)

            null -> error("unknown item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}

class TimeViewHolder(
    private val binding: CardTimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(time: Time) {
        binding.string.text = time.timeStr
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SimpleDateFormat")
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = Date.from(Instant.ofEpochSecond(post.published)).toString()
            content.text = post.content
            // в адаптере
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"
            imageAttachment.visibility = if (post.attachment != null) View.VISIBLE else View.GONE
            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            Glide.with(avatar)
                .load("${BASE_URL}/avatars/${post.authorAvatar}")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(avatar)

            post.attachment?.apply {
                when (AttachmentType.values().first()) {
                    AttachmentType.IMAGE -> {
                        Glide.with(imageAttachment)
                            .load("${BASE_URL}/media/${this.url}")
                            .placeholder(R.drawable.ic_loading_100dp)
                            .error(R.drawable.ic_error_100dp)
                            .timeout(10_000)
                            .into(imageAttachment)
                    }
                }
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    // TODO: if we don't have other options, just remove dots
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            views.setOnClickListener {
                onInteractionListener.onViews(post)
            }

            imageAttachment.setOnClickListener {
                onInteractionListener.openImage(post)
            }
        }
    }
}

class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class.java != newItem::class.java) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
