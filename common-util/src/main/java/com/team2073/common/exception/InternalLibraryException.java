package com.team2073.common.exception;

import com.team2073.common.util.StringUtil;

/**
 * An exception to be thrown by a library when the error is internal to the library and is
 * not the fault of the calling client. Denotes a bug in the library.
 * Libraries should extend this class and optionally override the getters to indicate steps
 * to report the bug.
 *
 * The message will automatically be prefixed with 'INTERNAL LIBRARY EXCEPTION: '.
 *
 * @author Preston Briggs
 */
public abstract class InternalLibraryException extends RuntimeException {

    private final String errorCode;

    public InternalLibraryException(String message) {
        super(message);
        this.errorCode = null;
    }

    public InternalLibraryException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public InternalLibraryException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public InternalLibraryException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getIssueTrackerUrl() {
        return null;
    }

    public String getIssueTrackerEmail() {
        return null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        String message = "INTERNAL LIBRARY EXCEPTION: " + super.getMessage();

        if (!StringUtil.isEmpty(getErrorCode()))
            message += " | Error code: [" + getErrorCode() + "]";

        if (!StringUtil.isEmpty(getIssueTrackerUrl()))
            message += " | Issue tracker url: [" + getIssueTrackerUrl() + "]";

        if (!StringUtil.isEmpty(getIssueTrackerEmail()))
            message += " | Issue tracker email: [" + getIssueTrackerEmail() + "]";

        return message;
    }
}
