package com.team2073.common.exception;

import com.team2073.common.util.Ex;
import com.team2073.common.util.Throw;

/**
 * Implementation of {@link Ex} with eagle-lib-specific exceptions.
 *
 * @author Preston Briggs
 */
public class EagleEx extends Ex {

    /** See {@link Throw} for usage. */
    public static EagleLibInternalException newInternal(String msg, Object... args) {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            return new EagleLibInternalException(String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            return new EagleLibInternalException(String.format(argMap.msg, argMap.args));
    }

    /** See {@link Throw} for usage. */
    public static EagleLibInternalException newInternal(String errorCode, String msg, Object... args) {
        Args argMap = new Args(msg, args);
        if (argMap.extractedThrowable != null)
            return new EagleLibInternalException(errorCode, String.format(argMap.msg, argMap.args), argMap.extractedThrowable);
        else
            return new EagleLibInternalException(errorCode, String.format(argMap.msg, argMap.args));
    }

}
