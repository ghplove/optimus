package com.ghp.impledemob

import android.content.Context
import android.util.Log
import com.ghp.annotation.OptimusService
import com.ghp.base.api.api.BBaseApi
import com.ghp.optimus.AbstractOptimusManager

/**
 * 生成的文件位置：Optimus\imple-demo-b\build\tmp\kapt3\classes\debug\META-INF\optimus
 */
@OptimusService(BBaseApi::class)
class DemoImplB : BBaseApi, AbstractOptimusManager() {
    private val TAG = "DemoImpl"
    override fun init() {
        Log.d(TAG, "init DemoImplB")
    }

    override fun initManager(context: Context?) {
        Log.d(TAG, "init BBaseApi")
    }

    override fun identifier(): Any {
        return TAG
    }

    override fun version(): String {
        return "1.0.0"
    }
}