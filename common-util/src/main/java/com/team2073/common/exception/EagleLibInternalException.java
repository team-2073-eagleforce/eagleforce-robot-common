package com.team2073.common.exception;

/**
 * eagle-lib's implementation of {@link InternalLibraryException}.
 *
 * @author Preston Briggs
 */
public class EagleLibInternalException extends InternalLibraryException {

    public EagleLibInternalException(String message) {
        super(message);
    }

    public EagleLibInternalException(String errorCode, String message) {
        super(errorCode, message);
    }

    public EagleLibInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public EagleLibInternalException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    @Override
    public String getIssueTrackerUrl() {
        // TODO: Once we have a public issue tracker, update this with the url
        return null;
    }

    @Override
    public String getIssueTrackerEmail() {
        return "Preston Briggs: pbriggs28@gmail.com";
    }
}
