package com.ghp.plugin;

import com.ghp.base.api.api.ABaseApi;
import com.ghp.base.api.api.BBaseApi;
import com.ghp.base.api.api.CBaseApi;
import com.ghp.base.api.plugin.IAEnginePlugin;
import com.ghp.base.api.plugin.IBEnginePlugin;
import com.ghp.base.api.plugin.ICEnginePlugin;

public class EngineManger {

    private static final IAEnginePlugin aEnginePlugin = EnginePluginManager.service(IAEnginePlugin.class);
    private static final IBEnginePlugin bEnginePlugin = EnginePluginManager.service(IBEnginePlugin.class);
    private static final ICEnginePlugin cEnginePlugin = EnginePluginManager.service(ICEnginePlugin.class);

    public static ABaseApi getAEngineInstance (){
        if (aEnginePlugin == null){
            return null;
        }else {
            return aEnginePlugin.getAEngineInstance();
        }
    }

    public static BBaseApi getBEngineInstance (){
        if (bEnginePlugin == null){
            return null;
        }else {
            return bEnginePlugin.getBEngineInstance();
        }
    }

    public static CBaseApi getCEngineInstance(){
        if (cEnginePlugin == null){
            return null;
        }else {
            return cEnginePlugin.getCEngineInstance();
        }
    }
}
