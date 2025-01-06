package com.example.swipeassignment.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.swipeassignment.data.MainRepository

// Upload worker to upload data to server using workmanager.
class UploadWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val repository: MainRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncUnsyncedProducts()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

}
