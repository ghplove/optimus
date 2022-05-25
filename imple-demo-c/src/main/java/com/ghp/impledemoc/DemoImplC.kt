package com.ghp.impledemoc

import android.util.Log
import com.ghp.base.api.BaseApi

class DemoImplC : BaseApi {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplC")
    }
}