package com.quanzikong.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 多线程管理类
 *
 * @author devinchen
 * @date 2016/10/13.
 */
public class ThreadUtil {

    /**
     * the number of threads to keep in the pool, even if they are idle,
     * unless {@code allowCoreThreadTimeOut} is set
     */
    private static final int CORE_POOL_SIZE = 5;
    /**
     * the maximum number of threads to allow in the pool
     */
    private static final int MAXIMUM_POOL_SIZE = 200;
    private static final long KEEP_ALIVE_TIME = 0L;
    private static final int BLOCKING_QUEUE_SIZE = 1024;
    private static final String THREAD_FACTORY_BUILDER_NAME_FORMAT = "devin-pool-%d";

    public static ExecutorService getExecutorService() {
        return new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE),
            new ThreadFactoryBuilder().setNameFormat(THREAD_FACTORY_BUILDER_NAME_FORMAT).build(),
            new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(
            CORE_POOL_SIZE,
            new ThreadFactoryBuilder().setNameFormat(THREAD_FACTORY_BUILDER_NAME_FORMAT).build(),
            new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 立即启动线程
     *
     * @param runnable Runnable
     */
    public static void startThreadImmediate(Runnable runnable) {
        ExecutorService executor = getExecutorService();
        executor.submit(runnable);
        executor.shutdown();
    }

    /**
     * 延时启动线程
     *
     * @param runnable Runnable
     * @param delay    long
     * @param unit     TimeUnit
     */
    public static void startThreadDelay(Runnable runnable, long delay, TimeUnit unit) {
        ScheduledExecutorService service = getScheduledExecutorService();
        service.schedule(runnable, delay, unit);
        service.shutdown();
    }
}
