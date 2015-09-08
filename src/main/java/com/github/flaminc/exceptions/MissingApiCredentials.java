package com.github.flaminc.exceptions;

public class MissingApiCredentials extends Exception {
    public MissingApiCredentials(Throwable cause) {
        super(cause);
    }

    public MissingApiCredentials(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingApiCredentials(String message) {
        super(message);
    }

    public MissingApiCredentials() {
    }
}
