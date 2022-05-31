package com.ghp.engine

import com.ghp.base.api.base.BaseApi
import com.ghp.base.api.DemoType
import com.ghp.plugin.EngineManger

class EngineImple{
    private var baseApi: BaseApi? = null
    private var type: DemoType = DemoType.DEMO_A

    companion object {
        val instance: EngineImple by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EngineImple()
        }
    }

    fun init(){
        baseApi = when (type) {
            DemoType.DEMO_A -> {
                EngineManger.getAEngineInstance()
            }
            DemoType.DEMO_B -> {
                EngineManger.getBEngineInstance()
            }
            DemoType.DEMO_C -> {
                EngineManger.getCEngineInstance()
            }
        }
        baseApi?.init()
    }
}