package com.github.jgriff.kuali.elevatordemo;

/**
 * Raised if a request is made against an elevator that is in a state that prevents it from being available to
 * comply with the request.  For example, if the elevator is in a {@link ElevatorOperationalState#MAINTENANCE} state.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public class ElevatorUnavailableException extends RuntimeException {
    public ElevatorUnavailableException() {
        super();
    }

    public ElevatorUnavailableException(String message) {
        super(message);
    }

    public ElevatorUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElevatorUnavailableException(Throwable cause) {
        super(cause);
    }

    protected ElevatorUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
