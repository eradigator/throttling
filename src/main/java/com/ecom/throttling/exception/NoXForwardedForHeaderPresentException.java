package com.ecom.throttling.exception;

public class NoXForwardedForHeaderPresentException extends IllegalArgumentException{

    public NoXForwardedForHeaderPresentException() {
        super("No X-Forwarded-For header provided");
    }
}
