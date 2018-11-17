package com.team2073.common.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Preston Briggs
 */
public class FutureWait<T> {

    private Logger log = LoggerFactory.getLogger(getClass());

    private final Future future;

    public FutureWait(Future<T> future) {
        this.future = future;
    }

    public void block(long timeoutMillis) {
        try {
            future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("Exception occurred waiting for Future#get(). Exception: ", e);
        }
    }

    public void block() {
        try {
            future.get();
        } catch (Exception e) {
            log.warn("Exception occurred waiting for Future#get(). Exception: ", e);
        }
    }

    public Future<T> getFuture() {
        return future;
    }
}
