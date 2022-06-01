package com.ghp.impledemoc

import android.content.Context
import android.util.Log
import com.ghp.annotation.OptimusService
import com.ghp.base.api.api.CBaseApi
import com.ghp.optimus.AbstractOptimusManager

@OptimusService(CBaseApi::class)
class DemoImplC : CBaseApi, AbstractOptimusManager() {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplC")
    }

    override fun initManager(context: Context?) {
        Log.d(TAG, "init CBaseApi")
    }

    override fun identifier(): Any {
        return TAG
    }

    override fun version(): String {
        return "1.0.0"
    }
}