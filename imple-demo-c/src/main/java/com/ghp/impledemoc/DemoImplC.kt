package com.ghp.impledemoc

import android.util.Log
import com.ghp.base.api.api.CBaseApi

class DemoImplC : CBaseApi {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplC")
    }
}