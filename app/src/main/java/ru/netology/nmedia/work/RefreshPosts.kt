package ru.netology.nmedia.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.repository.PostRepository

@HiltWorker
class RefreshPostsWorker @AssistedInject constructor(
    private val repository: PostRepository,
    @Assisted applicationContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val name = "ru.netology.work.RefreshPostsWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            repository.getAllAsync()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
