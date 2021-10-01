package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        //_data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            val old = _data.value?.posts.orEmpty()
            repository.saveAsync(it, object : PostRepository.SaveCallback {
                override fun onSuccess(post: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(posts = old))
                }
            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.likeByIdAsync(id, object : PostRepository.GetByIdCallback {
            override fun onSuccess(id: Long) {
                /*_data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) {
                                if (!it.likedByMe) {
                                    it.copy(likes = it.likes + 1, likedByMe = !it.likedByMe)
                                } else it.copy(likes = it.likes - 1, likedByMe = !it.likedByMe)
                            } else it
                        }
                    )
                )*/
                loadPosts()
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun unlikeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.unlikeByIdAsync(id, object : PostRepository.GetByIdCallback {
            override fun onSuccess(id: Long) {
                /*_data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) {
                                if (!it.likedByMe) {
                                    it.copy(likes = it.likes + 1, likedByMe = !it.likedByMe)
                                } else it.copy(likes = it.likes - 1, likedByMe = !it.likedByMe)
                            } else it
                        }
                    )
                )*/
                loadPosts()
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.removeByIdAsync(id, object : PostRepository.GetByIdCallback {
            override fun onSuccess(id: Long) {
                val posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                _data.postValue(
                    _data.value?.copy(posts = posts, empty = posts.isEmpty())
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun shareById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.shareByIdAsync(id, object : PostRepository.GetByIdCallback {
            override fun onSuccess(id: Long) {
                /*_data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) it.copy(shares = it.shares + 1) else it
                        })
                )*/
                loadPosts()
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun viewsById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.viewsByIdAsync(id, object : PostRepository.GetByIdCallback {
            override fun onSuccess(id: Long) {
                /*_data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) it.copy(views = it.views + 1) else it
                        })
                )*/
                loadPosts()
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }
}
