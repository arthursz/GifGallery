package com.arthurzettler.giphygallery.data

sealed class Result<out V> {
    data class Success<out T>(val value: T) : Result<T>()
    object Failure : Result<Nothing>()
}