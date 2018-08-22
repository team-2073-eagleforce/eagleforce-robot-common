package com.team2073.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author pbriggs
 */
public abstract class ThreadUtil {

    private static Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.warn(String.format("InterruptedException thrown while attempting to sleep for [%s] millis. Thread: [%s]",
                    millis, Thread.currentThread().getName()), e);
        }
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param executor the executor to invoke awaitTermination on
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return {@code true} if this executor terminated and
     *         {@code false} if the timeout elapsed before termination
     */
    public static boolean awaitTermination(ExecutorService executor, long timeout, TimeUnit unit) {
        try {
            return executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            log.warn(String.format("InterruptedException thrown while attempting to call awaitTermination with delay of [%s %s]. Thread: [%s]",
                    timeout, unit.toString(), Thread.currentThread().getName()), e);
            return false;
        }
    }
}
