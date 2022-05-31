package com.ghp.impledemoc;


import com.ghp.base.api.api.CBaseApi;
import com.ghp.base.api.plugin.ICEnginePlugin;

public class CEnginePlugin implements ICEnginePlugin {

    @Override
    public CBaseApi getCEngineInstance() {
        return new DemoImplC();
    }
}
