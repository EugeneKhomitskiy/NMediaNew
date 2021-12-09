package ru.netology.nmedia.application

import android.app.Application
import androidx.hilt.work.HiltWorker
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.work.RefreshPostsWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class NMediaApplication : Application(), Configuration.Provider {
    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var auth: AppAuth

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        setupAuth()
        setupWork()
    }

    private fun setupAuth() {
        appScope.launch {
            auth.sendPushToken()
        }
    }

    private fun setupWork() {
        appScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<RefreshPostsWorker>(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniquePeriodicWork(
                RefreshPostsWorker.name,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}