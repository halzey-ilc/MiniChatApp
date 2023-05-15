package com.example.minichatapp.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
   open class Loading<T> : Resource<T>()
   open class Success<T>(data: T) : Resource<T>(data = data)
   open class Error<T>(message: String) : Resource<T>(message = message)
   open class Empty<T>(message: String? = null) : Resource<T>(message = message)
}