package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.TerminalSeparatorType
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.PostTime
import ru.netology.nmedia.enumeration.RetryType
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia.work.RemovePostWorker
import ru.netology.nmedia.work.SavePostWorker
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.random.Random

private val empty = Post(
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

private val noPhoto = PhotoModel()

private val currentTime = OffsetDateTime.now().toEpochSecond()
private const val TODAY = 86400
private const val YESTERDAY = 172800

private const val TODAY_ID = 539802L
private const val YESTERDAY_ID = 939384L
private const val LAST_WEEK_ID = 892832L

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val workManager: WorkManager,
) : ViewModel() {

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = cached.map {
        it.insertSeparators(TerminalSeparatorType.SOURCE_COMPLETE) { previous, next ->
            val diff = currentTime - (next?.published ?: 0)
            when {
                next == null -> null
                previous == null -> {
                    when {
                        diff <= TODAY -> {
                            Time(TODAY_ID, PostTime.TODAY)
                        }
                        diff < TODAY && diff <= YESTERDAY -> {
                            Time(YESTERDAY_ID, PostTime.YESTERDAY)
                        }
                        else -> {
                            Time(LAST_WEEK_ID, PostTime.LAST_WEEK)
                        }
                    }
                }
                diff in (TODAY + 1) until YESTERDAY -> {
                    Time(YESTERDAY_ID, PostTime.YESTERDAY)
                }
                diff > YESTERDAY -> {
                    Time(LAST_WEEK_ID, PostTime.LAST_WEEK)
                }
                previous.id.rem(5) == 0L -> {
                    Ad(Random.nextLong(), "figma.jpg")
                }
                else -> null
            }
        }
    }

/*    @ExperimentalCoroutinesApi
    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }*/

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            //repository.getAllAsync()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun loadNewPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getNewPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            //repository.getAllAsync()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.saveWork(
                        it, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SavePostWorker.postKey to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeByIdAsync(id)
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, retryType = RetryType.LIKE, retryId = id)
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            repository.unlikeByIdAsync(id)
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, retryType = RetryType.UNLIKE, retryId = id)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            val data = workDataOf(RemovePostWorker.postKey to id)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(request)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            e.printStackTrace()
            _dataState.value = FeedModelState(error = true)
        }
    }
}
