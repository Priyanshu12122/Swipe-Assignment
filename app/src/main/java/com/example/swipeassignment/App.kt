package com.example.swipeassignment

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.swipeassignment.di.koinModule
import com.example.swipeassignment.workmanager.KoinWorkerFactory
import com.example.swipeassignment.workmanager.UploadWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.TimeUnit

// Application class
class App : Application(), Configuration.Provider {


    override fun onCreate() {
        super.onCreate()

//        Starting koin in oncreate
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            workManagerFactory()
            modules(koinModule)

        }

        schedulePeriodicUploadWorker()

    }


//    Function to schedule worker for uploading data to server.
    private fun schedulePeriodicUploadWorker() {
        val workRequest = PeriodicWorkRequestBuilder<UploadWorker>(30, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UploadWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }



    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()


}

