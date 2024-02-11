package com.byronlin.pokemo.viewmodel

interface ViewModelDispatcherProvider {
    fun getDispatcher(): kotlinx.coroutines.CoroutineDispatcher
}