package com.diamond.appcliente.util

sealed class CacheAwareData<out T> {
    data class Stale<T>(val data: T) : CacheAwareData<T>()
    data class Fresh<T>(val data: T) : CacheAwareData<T>()
}
