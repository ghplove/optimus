package com.ghp.plugin;

import android.util.Log;

import java.util.HashMap;

public class EnginePluginManager {
    private static String[] providers = new String[]{
            "com.ghp.impledemoa.APluginManager",
            "com.ghp.impledemob.BPluginManager",
            "com.ghp.impledemoc.CPluginManager"
    };

    private static final HashMap<Class, Object> classObjectHashMap = new HashMap<>(providers.length);

    public static <T> T service(Class<T> a2) {
        return (T) classObjectHashMap.get(a2);
    }

    static {
        loadRouter();
    }
    private static void loadRouter() {
        for (String provider : providers) {
            try {
                HolderPlugin basePlugin = (HolderPlugin) Class.forName(provider).newInstance();
                basePlugin.configure();
                Log.d("EnginePluginManager", provider + " loadRouter!");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public abstract static class HolderPlugin {

        protected abstract void configure();

        protected static void registerService(Class c, Object object) {
            classObjectHashMap.put(c, object);
        }
    }
}
