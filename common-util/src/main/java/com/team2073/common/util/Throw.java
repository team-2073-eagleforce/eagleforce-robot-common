package com.team2073.common.util;

import ch.qos.logback.classic.spi.EventArgUtil;
import ch.qos.logback.classic.spi.LoggingEvent;

import java.io.IOException;

/**
 * Utility to throw common exceptions and automatically format Strings in their messages.<br/>
 * Accepts either logger notation "{}" or String.format notation "%s" (and friends). <br/>
 * Accepts a Throwable the same way a logger accepts it, as the last argument.
 * <br/>
 * <h3>Example usage:</h3>
 * String.format syntax:
 * <pre>Throw.ioEx("Error writing data. File: [%s]. Data: [%s].", filePath, data);</pre>
 * <br/>
 * Logger syntax:
 * <pre>Throw.ioEx("Error writing data. File: [{}]. Data: [{}].", filePath, data);</pre>
 * <br/>
 * With exception:
 * <pre>Throw.ioEx("Error writing data. File: [{}]. Data: [{}].", filePath, data, e);</pre>
 * <br/>
 *
 * @author pbriggs
 */
public abstract class Throw {

    /** See {@link Throw} for usage. */
	public static void illegalState(String msg, Object... args) {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            throw new IllegalStateException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            throw new IllegalStateException(String.format(argMap.msg, argMap.args));
	}

    public static void ioEx(String msg, Object... args) throws IOException {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            throw new IOException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            throw new IOException(String.format(argMap.msg, argMap.args));
    }

    private static class Args {
        final String msg;
        final Object[] args;
        final Throwable extractedThrowable;

        /**
         * Stolen from {@link LoggingEvent#extractThrowableAnRearrangeArguments(Object[])}. <br/><br/>
         * shhhhh...
         */
        public Args(String msg, Object[] args) {
            this.msg = LogUtil.convertLogToStrFmt(msg);
            this.extractedThrowable = EventArgUtil.extractThrowable(args);;
            if (EventArgUtil.successfulExtraction(this.extractedThrowable))
                this.args = EventArgUtil.trimmedCopy(args);
            else
                this.args = args;
        }
    }
}
