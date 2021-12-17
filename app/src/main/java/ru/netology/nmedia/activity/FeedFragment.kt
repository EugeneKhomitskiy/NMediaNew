package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.RetryType
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val viewModelAuth: AuthViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val bundle = Bundle().apply { putString("content", post.content) }
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment, bundle)
            }

            override fun onLike(post: Post) {
                if (viewModelAuth.authenticated) {
                    if (!post.likedByMe) viewModel.likeById(post.id) else viewModel.unlikeById(post.id)
                } else {
                    Toast.makeText(
                        activity,
                        R.string.error_auth,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun openImage(post: Post) {
                val bundle = Bundle().apply { putString("url", post.attachment?.url) }
                findNavController().navigate(R.id.action_feedFragment_to_attachmentFragment, bundle)
            }
        })
        binding.list.adapter = adapter
        binding.list.animation = null

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_loading) {
                        when (state.retryType) {
                            RetryType.LIKE -> viewModel.likeById(state.retryId)
                            RetryType.UNLIKE -> viewModel.unlikeById(state.retryId)
                            else -> viewModel.refreshPosts()
                        }
                    }
                    .show()
            }
        }

        viewModelAuth.data.observe(viewLifecycleOwner) { adapter.refresh() }

/*        viewModel.newerCount.observe(viewLifecycleOwner) {
            with(binding.buttonNewPosts) {
                if (it > 0) {
                    text = "${getString(new_posts)} $it"
                    visibility = View.VISIBLE
                }
            }
        }*/

        binding.buttonNewPosts.setOnClickListener {
            viewModel.loadNewPosts()
            binding.buttonNewPosts.visibility = View.GONE
        }

        binding.fab.setOnClickListener {
            if (viewModelAuth.authenticated) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                Toast.makeText(
                    activity,
                    R.string.error_auth,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }
}
