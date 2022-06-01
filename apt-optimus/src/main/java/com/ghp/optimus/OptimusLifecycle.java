package com.ghp.optimus;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Keep
public class OptimusLifecycle implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "OptimusLifecycle";

    private final AtomicInteger createdCounter = new AtomicInteger();
    private final AtomicBoolean isChangingConfigurations = new AtomicBoolean(false);

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (createdCounter.getAndIncrement() == 0 && !isChangingConfigurations.getAndSet(activity.isChangingConfigurations())) {
            initOptimus(activity.getApplication());
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        createdCounter.getAndDecrement();
        isChangingConfigurations.set(activity.isChangingConfigurations());
    }

    private void initOptimus(Application application) {
        OptimusExecutor.getInstance().executorCallerRunsPolicy(() -> {
            // 反射调用垂直化SDK统一入口的初始化方法
            try {
                final Class<?> optimusSdkClass = Class.forName("com.ghp.optimus.OptimusSdk");
                final Field sdkInfos = optimusSdkClass.getDeclaredField("SDK_INFOS");
                sdkInfos.setAccessible(true);
                Class<?> initCallbackClass = Class.forName("com.ghp.optimus.InitCallback");
                Method init = optimusSdkClass.getMethod("init", Context.class, initCallbackClass);
                init.invoke(null, application, null);
            } catch (Throwable ignore) {
            }
        });
    }

}
