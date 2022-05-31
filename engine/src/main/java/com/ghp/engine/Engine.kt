package com.ghp.engine

import com.ghp.base.api.BaseApi

class Engine : BaseApi{

    companion object {
        val instance: Engine by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Engine()
        }
    }

    override fun init() {
        EngineImple.instance.init()
    }

}