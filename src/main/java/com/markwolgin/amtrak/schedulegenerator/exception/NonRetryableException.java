package com.markwolgin.amtrak.schedulegenerator.exception;

public class NonRetryableException extends RuntimeException {

    public NonRetryableException(Throwable throwable) {
        super(throwable);
    }

}
