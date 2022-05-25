package com.ghp.impledemob

import android.util.Log
import com.ghp.base.api.BaseApi

class DemoImplB : BaseApi {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplB")
    }
}