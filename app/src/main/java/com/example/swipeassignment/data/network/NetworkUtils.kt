package com.example.swipeassignment.data.network


//  Generic wrapper class for handling different states of data like Success, Error or Loading
//  when making network requests.

sealed class Response<T> {
    data class Success<T>(val data: T) : Response<T>()
    data class Error<T>(val msg: String) : Response<T>()
    data class Loading<T>(val data: T? = null) : Response<T>()
}