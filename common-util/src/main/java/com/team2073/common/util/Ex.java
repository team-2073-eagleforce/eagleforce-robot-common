package com.team2073.common.util;

import ch.qos.logback.classic.spi.EventArgUtil;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.team2073.common.exception.NotYetImplementedException;

import java.io.IOException;

/**
 * Utility to construct common exceptions and automatically format Strings in their messages.
 * Accepts either logger notation "{}" or String.format notation "%s" (and friends).
 * Accepts a Throwable the same way a logger accepts it, as the last argument.
 * <h3>Example usage:</h3>
 * <i>(Square brackets "[ ]" are a good habit but not required)</i>
 * String.format syntax:
 * <pre>throw Ex.illegalState("Error writing data. File: [%s]. Data: [%s].", filePath, data);</pre>
 * Logger syntax:
 * <pre>throw Ex.illegalState("Error writing data. File: [{}]. Data: [{}].", filePath, data);</pre>
 * With exception:
 * } catch (Exception e) {
 *     throw Ex.illegalState("Error writing data. File: [{}]. Data: [{}].", filePath, data, e);
 *
 *
 * @author Preston Briggs
 */
public abstract class Ex {

    /** See {@link Ex} for usage. */
    public static IllegalStateException illegalState(String msg, Object... args) {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            return new IllegalStateException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            return new IllegalStateException(String.format(argMap.msg, argMap.args));
    }

    /** See {@link Ex} for usage. */
    public static IllegalArgumentException illegalArg(String msg, Object... args) {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            return new IllegalArgumentException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            return new IllegalArgumentException(String.format(argMap.msg, argMap.args));
    }

    /** See {@link Ex} for usage. NOTE: This exception does not accept 'caused by' exceptions so it will not
     * parse a Throwable from the provided args. */
    public static NotYetImplementedException notImplemented(String methodName) {
        return new NotYetImplementedException(methodName);
    }

    /** See {@link Ex} for usage. */
    public static IOException io(String msg, Object... args) throws IOException {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            return new IOException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            return new IOException(String.format(argMap.msg, argMap.args));
    }

    public static class Args {
        public final String msg;
        public final Object[] args;
        public final Throwable extractedThrowable;

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
