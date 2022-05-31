package com.ghp.impledemob;


import com.ghp.base.api.api.BBaseApi;
import com.ghp.base.api.plugin.IBEnginePlugin;

public class BEnginePlugin implements IBEnginePlugin {

    @Override
    public BBaseApi getBEngineInstance() {
        return new DemoImplB();
    }
}
