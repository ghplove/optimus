package com.ghp.impledemoa

import android.util.Log
import com.ghp.base.api.BaseApi

class DemoImplA : BaseApi {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplA")
    }
}