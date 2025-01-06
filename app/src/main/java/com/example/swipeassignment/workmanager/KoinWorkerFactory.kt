package com.example.swipeassignment.workmanager

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.swipeassignment.data.MainRepository
import org.koin.core.context.GlobalContext


// Koin worker factory to inject workmanager with help of koin.
class KoinWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        Log.d("KoinWorkerFactory", "Attempting to create worker: $workerClassName")

        return try {
            val koin = GlobalContext.get()
            Log.d("KoinWorkerFactory", "Got Koin context: $koin")

            when (workerClassName) {
                UploadWorker::class.java.name -> {
                    val repository = koin.get<MainRepository>()
                    Log.d("KoinWorkerFactory", "Got repository instance: $repository")
                    UploadWorker(appContext, workerParameters, repository)
                }
                else -> {
                    Log.d("KoinWorkerFactory", "Unknown worker class: $workerClassName")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("KoinWorkerFactory", "Error creating worker", e)
            throw e  // Rethrow to see the error in WorkManager's logs
        }
    }
}