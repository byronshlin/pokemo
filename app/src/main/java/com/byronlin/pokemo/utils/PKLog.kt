package com.byronlin.pokemo.utils

import android.util.Log

object PKLog {
    private const val TAG = "PKLog"

    fun v(subTag: String, desc: String) {
        Log.v(TAG, "$subTag: $desc")
    }

    fun e(subTag: String, desc: String, e: Throwable) {
        Log.e(TAG, "$subTag: $desc", e)
    }
}