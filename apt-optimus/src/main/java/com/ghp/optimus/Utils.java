package com.ghp.optimus;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utils {
    private static final String META_INF_PATH = "META-INF/optimus/";
    public static Application getApplication() {
        try {
            Class<?> activitythread = Class.forName("android.app.ActivityThread");

            Method m_currentActivityThread = activitythread
                    .getDeclaredMethod("currentActivityThread");
            Field f_mInitialApplication = activitythread.getDeclaredField("mInitialApplication");
            f_mInitialApplication.setAccessible(true);
            Object current = m_currentActivityThread.invoke(null);

            Object app = f_mInitialApplication.get(current);

            return (Application) app;

        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 从META-INF/optimus里读取
     *
     * @param context
     * @return
     */
    public static List<String> getSdkClassNames(Context context) {
        List<String> sdkClassNames = new ArrayList<>();
        ApplicationInfo appinfo = context.getApplicationInfo();
        File apkFile = new File(appinfo.sourceDir);
        if (!apkFile.exists()) return sdkClassNames;
        String channel = "";
        ZipFile zipFile= null;
        try {
            zipFile = new ZipFile(apkFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name == null || name.trim().length() == 0 || !name.startsWith(META_INF_PATH)) {
                    continue;
                }
                name = name.replace(META_INF_PATH, "");
                if (!TextUtils.isEmpty(name)) {
                    sdkClassNames.add(name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sdkClassNames;
    }
    public static boolean isMainProgress(Context context) {
        try {
            return TextUtils.equals(getProcessName(context), context.getPackageName());
        } catch (Exception e) {
//            L.w(OptimusSdk.TAG, "check main progress error: ", e);
        }
        return false;
    }


    private static String getProcessName(Context context) {
        String processName = getProcessNameFromActivityThread();
        if (TextUtils.isEmpty(processName)) {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (mActivityManager != null) {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityManager
                        .getRunningAppProcesses();
                if (runningAppProcesses != null) {
                    for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
                        if (appProcess.pid == pid) {
                            return appProcess.processName;
                        }
                    }
                }
            }
        }
        return processName;
    }

    public static String getProcessNameFromActivityThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            return Application.getProcessName();
        else {
            try {
                Class<?> activityThread = Class.forName("android.app.ActivityThread");
                // Before API 18, the method was incorrectly named "currentPackageName", but it still returned the process name
                // See https://github.com/aosp-mirror/platform_frameworks_base/commit/b57a50bd16ce25db441da5c1b63d48721bb90687
                String methodName = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ? "currentProcessName" : "currentPackageName";
                Method getProcessName = activityThread.getDeclaredMethod(methodName);
                return (String) getProcessName.invoke(null);
            } catch (Throwable e) {
            }
        }
        return "";
    }
}
