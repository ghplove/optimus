package com.ghp.impledemoc;

import com.ghp.base.api.plugin.ICEnginePlugin;
import com.ghp.plugin.EnginePluginManager;

public class CPluginManager extends EnginePluginManager.HolderPlugin {
    private static final CEnginePlugin cEnginePlugin = new CEnginePlugin();
    @Override
    protected void configure() {
        registerService(ICEnginePlugin.class, cEnginePlugin);
    }
}
