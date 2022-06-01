package com.ghp.impledemoa;

import android.content.Context;
import android.util.Log;

import com.ghp.annotation.OptimusService;
import com.ghp.base.api.api.ABaseApi;
import com.ghp.optimus.AbstractOptimusManager;

/**
 * 生成的文件位置：Optimus\imple-demo-a\build\intermediates\javac\debug\classes\META-INF\optimus
 */
@OptimusService(ABaseApi.class)
public class DemoImplA extends AbstractOptimusManager implements ABaseApi {
    private final String TAG = "DemoImpl";
    @Override
    public void init() {
        Log.d(TAG, "init DemoImplA");
    }

    @Override
    public void initManager(Context context) {
        Log.d(TAG, "init ABaseApi");
    }

    @Override
    public Object identifier() {
        return TAG;
    }

    @Override
    public String version() {
        return "1.0.0";
    }
}
