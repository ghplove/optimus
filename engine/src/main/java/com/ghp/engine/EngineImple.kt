package com.ghp.engine

import com.ghp.base.api.BaseApi
import com.ghp.base.api.DemoType
import com.ghp.impledemoa.DemoImplA
import com.ghp.impledemob.DemoImplB
import com.ghp.impledemoc.DemoImplC

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
                DemoImplA()
            }
            DemoType.DEMO_B -> {
                DemoImplB()
            }
            DemoType.DEMO_C -> {
                DemoImplC()
            }
        }
        baseApi?.init()
    }
}