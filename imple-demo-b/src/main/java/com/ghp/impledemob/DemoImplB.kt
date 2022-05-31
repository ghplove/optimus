package com.ghp.impledemob

import android.util.Log
import com.ghp.base.api.api.BBaseApi

class DemoImplB : BBaseApi {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplB")
    }
}