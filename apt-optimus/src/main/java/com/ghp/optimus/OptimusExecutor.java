package com.ghp.optimus;

import androidx.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class OptimusExecutor {

    private static final int CPU_COUNT = 3;
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 4 + 1;
    private static final long KEEP_ALIVE = 1;

    private static volatile OptimusExecutor mOptimusExecutor;
    private final ThreadPoolExecutor.CallerRunsPolicy mCallerRunsPolicy;
    private final ThreadPoolExecutor.DiscardOldestPolicy mDiscardOldestPolicy;
    private ThreadPoolExecutor mExecutorService;
    private static final int TYPE_CALLER_RUNS_POLICY = 2;

    private static final BlockingQueue<Runnable> mPoolWorkQueue = new LinkedBlockingQueue<>(
            128);

    public static OptimusExecutor getInstance() {
        if (mOptimusExecutor == null) {
            synchronized (OptimusExecutor.class) {
                if (mOptimusExecutor == null) {
                    mOptimusExecutor = new OptimusExecutor();
                }
            }
        }
        return mOptimusExecutor;
    }

    private OptimusExecutor(){
        mCallerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
        mDiscardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy();
        mExecutorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, mPoolWorkQueue, new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "Thread #" + mCount.getAndIncrement());
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (r instanceof OptimusRunnable) {
                    OptimusRunnable runable = (OptimusRunnable) r;
                    if (runable.type == TYPE_CALLER_RUNS_POLICY) {
                        mCallerRunsPolicy.rejectedExecution(r, executor);
                    }
                } else {
                    mDiscardOldestPolicy.rejectedExecution(r, executor);
                }
            }
        });
        mExecutorService.allowCoreThreadTimeOut(true);
    }

    public void executorCallerRunsPolicy(Runnable runnable) {
        getExecutorService().execute(new OptimusRunnable(TYPE_CALLER_RUNS_POLICY, runnable));
    }

    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

    public static class OptimusRunnable implements Runnable {

        private final Runnable runnable;
        private final int type;

        public OptimusRunnable(int type, Runnable runnable) {
            this.runnable = runnable;
            this.type = type;
        }

        @Override
        public void run() {
            if (runnable != null) {
                runnable.run();
            }
        }
    }
}
