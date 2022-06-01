package com.ghp.optimus;

import android.content.Context;

public interface AbstractOptimusManager {

    /**
     * 统一的初始化接口
     *
     * @param context 上下文
     */
//    @Deprecated()
    public void initManager(Context context);

    /**
     * 当前SDK的标识符
     *
     * @return SDK标识符
     */
    public Object identifier();

    /**
     * 当前SDK版本
     *
     * @return SDK版本
     */
    public String version();
}
