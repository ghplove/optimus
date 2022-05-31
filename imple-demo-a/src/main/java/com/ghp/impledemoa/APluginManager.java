package com.ghp.impledemoa;

import com.ghp.base.api.plugin.IAEnginePlugin;
import com.ghp.plugin.EnginePluginManager;

public class APluginManager extends EnginePluginManager.HolderPlugin {
    private static final AEnginePlugin aEnginePlugin = new AEnginePlugin();
    @Override
    protected void configure() {
        registerService(IAEnginePlugin.class, aEnginePlugin);
    }
}
