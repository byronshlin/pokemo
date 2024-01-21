package com.byronlin.pokemo.utils

import android.util.Log

object PKLog {
    private const val TAG = "PKLog"

    fun v(subTag: String, desc: String) {
        Log.v(TAG, "$subTag: $desc")
    }
}