package com.ghp.engine

import com.ghp.base.api.DemoType
import com.ghp.base.api.api.ABaseApi
import com.ghp.base.api.api.BBaseApi
import com.ghp.base.api.api.CBaseApi
import com.ghp.base.api.base.BaseApi
import com.ghp.optimus.OptimusSdk

class EngineImple{
    private var baseApi: BaseApi? = null
    private var type: DemoType = DemoType.DEMO_B

    companion object {
        val instance: EngineImple by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EngineImple()
        }
    }

    fun init(){
        baseApi = when (type) {
            DemoType.DEMO_A -> {
                OptimusSdk.getManager(ABaseApi::class.java)
            }
            DemoType.DEMO_B -> {
                OptimusSdk.getManager(BBaseApi::class.java)
            }
            DemoType.DEMO_C -> {
                OptimusSdk.getManager(CBaseApi::class.java)
            }
        }
        baseApi?.init()
    }
}