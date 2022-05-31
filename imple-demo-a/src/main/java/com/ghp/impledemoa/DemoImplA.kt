package com.ghp.impledemoa

import android.util.Log
import com.ghp.base.api.api.ABaseApi

class DemoImplA : ABaseApi {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplA")
    }
}