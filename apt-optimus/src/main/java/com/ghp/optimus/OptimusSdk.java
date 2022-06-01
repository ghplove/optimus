package com.ghp.optimus;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OptimusSdk {

    static final String TAG = "OptimusSdk";

    private static final Map<String, AbstractOptimusManager> MANAGER_MAP = new HashMap<>();
    private static final Map<String, OptimusSdkInfo> SDK_INFOS = new HashMap<>();
    private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Object lock = new Object();
    private static volatile boolean initialized;

    /**
     * 初始化方法，带结果回调
     *
     * @param context  context
     * @param callback callback
     */
    public static void init(Context context, InitCallback callback) {
        if (context == null) {
            if (callback != null)
                callback.onResult(InitCallback.CONTEXT_NULL);
            Log.w(TAG, "Context is null!");
            return;
        }
        SINGLE_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (initialized) {
                        if (callback != null)
                            callback.onResult(InitCallback.ALREADY_INITIALED);
                        Log.i(TAG, "OptimusSdk already initialized");
                        return;
                    }
                    List<String> sdkClassNames = Utils.getSdkClassNames(context);
                    for (String sdkClassName : sdkClassNames) {
                        Log.d(TAG, "sdkClassName: " + sdkClassName);
                        OptimusLoader<?> load = null;
                        try {
                            Class<?> sdkClass = Class.forName(sdkClassName);
                            load = OptimusLoader.load(sdkClass, context.getClassLoader());
                        } catch (Throwable e) {
                            Log.e(TAG, e.toString());
                            if (load == null) continue;
                        }
                        for (Object object : load) {
                            if (object instanceof AbstractOptimusManager) {
                                AbstractOptimusManager manager = (AbstractOptimusManager) object;
                                Log.d(TAG, "manager: " + manager);
                                OptimusSdkInfo optimusSdkInfo = new OptimusSdkInfo();
                                optimusSdkInfo.identifier = object.getClass().getDeclaredMethod("identifier").invoke(manager);
                                optimusSdkInfo.version = manager.version();
                                SDK_INFOS.put(sdkClassName, optimusSdkInfo);
                                MANAGER_MAP.put(sdkClassName, manager);
                            }
                        }
                    }
                } catch (Throwable e) {
                    Log.w(TAG, e.toString());
                    if (callback != null)
                        callback.onResult(InitCallback.CLASS_NOT_FOUND);
                    return;
                }
                try {
                    initialized = true;
                    if (!MANAGER_MAP.isEmpty()) {
                        Log.d(TAG, "MANAGER_MAP = " + MANAGER_MAP);
                        initAllOptimusManager(context);
                    }
                    Log.i(TAG, "initialize success.");
                    if (callback != null)
                        callback.onResult(InitCallback.SUCCESS);
                } catch (Throwable e) {
                    if (callback != null)
                        callback.onResult(InitCallback.UNEXPECTED_EXCEPTION);
                }

            }
        });
    }

    public static void init(Context context) {
        init(context, null);
    }

    private static void initAllOptimusManager(Context context) {
        for (String managerName : MANAGER_MAP.keySet()) {
            AbstractOptimusManager manager = MANAGER_MAP.get(managerName);
            try {
                if (manager != null)
                    manager.initManager(context);
            } catch (Throwable e) {
                Log.w(TAG, manager + " init " + e.toString());
            }
        }
    }


    /**
     * 根据接口获取实现类
     *
     * @param clazz SDK接口类
     */
    public static <T> void getManager(final Class<T> clazz, final Callback<T> callback) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null!");
        }
        Log.i(TAG, "getManager with init");
        init(Utils.getApplication(), new InitCallback() {
            @Override
            public void onResult(int result) {
                Log.i(TAG, "getManager with init result: " + result);
                AbstractOptimusManager holder = MANAGER_MAP.get(clazz.getName());
                if (callback != null)
                    callback.onResult((T) holder);
            }
        });
    }

    /**
     * 根据传入的接口类，返回实例。
     * <p>
     * 此接口为异步转同步。
     *
     * @param clazz SDK接口类
     */
    public static <T> T getManager(Class<T> clazz) {
        if (initialized) {
            return (T) MANAGER_MAP.get(clazz.getName());
        } else {
            final Object[] manager = new Object[]{null};
            synchronized (lock) {
                getManager(clazz, new Callback<T>() {
                    @Override
                    public void onResult(T t) {
                        manager[0] = t;
                        notifyReturn();
                    }
                });
                waitUntilReturn();
                Log.d(TAG, "return service after init");
            }
            return (T) manager[0];
        }
    }

    private static void waitUntilReturn() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignore) {
            }
        }
    }

    private static void notifyReturn() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }


    public interface Callback<T> {
        void onResult(T t);
    }
}
