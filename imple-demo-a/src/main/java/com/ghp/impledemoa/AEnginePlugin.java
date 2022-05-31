package com.ghp.impledemoa;


import com.ghp.base.api.api.ABaseApi;
import com.ghp.base.api.plugin.IAEnginePlugin;

public class AEnginePlugin implements IAEnginePlugin {

    @Override
    public ABaseApi getAEngineInstance() {
        return new DemoImplA();
    }
}
