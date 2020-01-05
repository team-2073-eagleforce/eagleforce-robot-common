package com.team2073.common.util;

import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.util.Ex.Args;

import java.io.IOException;

/**
 * Deprecated: Use {@link Ex} instead.
 *
 * Utility to throw common exceptions and automatically format Strings in their messages.
 * Accepts either logger notation "{}" or String.format notation "%s" (and friends).
 * Accepts a Throwable the same way a logger accepts it, as the last argument.
 * Example usage (Square brackets "{@literal [} {@literal ]}" not required):
 *
 * @author Preston Briggs
 * @deprecated Use {@link Ex} instead
 */
@Deprecated
public abstract class Throw {

    /** @deprecated Use {@link Ex#illegalState(String, Object...)} instead. */
    @Deprecated
    public static void illegalState(String msg, Object... args) {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            throw new IllegalStateException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            throw new IllegalStateException(String.format(argMap.msg, argMap.args));
    }

    /** @deprecated Use {@link Ex#illegalArg(String, Object...)} instead. */
    @Deprecated
    public static void illegalArg(String msg, Object... args) {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            throw new IllegalArgumentException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            throw new IllegalArgumentException(String.format(argMap.msg, argMap.args));
    }

    @Deprecated
    public static void notImplemented(String msg, Object... args) {
        Args argMap = new Args(msg, args);
        throw new NotYetImplementedException(String.format(argMap.msg, argMap.args));
    }

    /** @deprecated Use {@link Ex#io(String, Object...)} instead. */
    @Deprecated
    public static void ioEx(String msg, Object... args) throws IOException {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            throw new IOException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            throw new IOException(String.format(argMap.msg, argMap.args));
    }
}
