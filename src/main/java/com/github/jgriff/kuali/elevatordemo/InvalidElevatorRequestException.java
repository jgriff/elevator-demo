package com.github.jgriff.kuali.elevatordemo;

/**
 * Exception signaling an invalid request was made for an elevator.  Typically, this could be the for things
 * like the first floor requesting to go DOWN, or the top floor requesting to go UP.  But could be used for any
 * request that is deemed "invalid".  
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public class InvalidElevatorRequestException extends RuntimeException {
    public InvalidElevatorRequestException() {
        super();
    }

    public InvalidElevatorRequestException(String message) {
        super(message);
    }

    public InvalidElevatorRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidElevatorRequestException(Throwable cause) {
        super(cause);
    }

    protected InvalidElevatorRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
