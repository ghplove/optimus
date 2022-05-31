package com.ghp.impledemob;

import com.ghp.base.api.plugin.IBEnginePlugin;
import com.ghp.plugin.EnginePluginManager;

public class BPluginManager extends EnginePluginManager.HolderPlugin {
    private static final BEnginePlugin bEnginePlugin = new BEnginePlugin();
    @Override
    protected void configure() {
        registerService(IBEnginePlugin.class, bEnginePlugin);
    }
}
