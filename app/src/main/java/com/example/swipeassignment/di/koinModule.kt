package com.example.swipeassignment.di

import androidx.room.Room
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.swipeassignment.data.MainRepository
import com.example.swipeassignment.data.NetworkMonitor
import com.example.swipeassignment.data.database.ProductDatabase
import com.example.swipeassignment.data.network.ApiService
import com.example.swipeassignment.data.network.NetworkConstants
import com.example.swipeassignment.ui.screens.add_product_screen.AddProductViewModel
import com.example.swipeassignment.ui.screens.list_screen.ListScreenViewModel
import com.example.swipeassignment.workmanager.KoinWorkerFactory
import com.example.swipeassignment.workmanager.UploadWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Koin module to provide dependency injection for all viewmodel's, repositories etc..
val koinModule = module {


//    Singleton Retrofit instance to make network calls.
    single {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    single {
        NetworkMonitor(context = get())
    }


//  Singleton room database dependency
    single {
        Room.databaseBuilder(
            get(),
            ProductDatabase::class.java,
            "Product Database"
        )
            .build()


    }

//    Room dao dependency
    single { get<ProductDatabase>().dao }

//    Singleton MainRepository dependency.
    single {
        MainRepository(get(), get(), get(), get())
    }

//    ListScreenViewModel dependency.
    viewModel {
        ListScreenViewModel(get())
    }

//    AddProductViewModel dependency
    viewModel {
        AddProductViewModel(get(), get())
    }


    //WorkManager instance
    factory { WorkManager.getInstance(get()) }

    // Worker Factory dependency.
    factory<WorkerFactory> {
        KoinWorkerFactory()
    }

//    WorkManager dependency
    worker { UploadWorker(get(), get(), get()) }



}