package io.esev.grpc.server;

public class CustomerException extends Exception {

    public CustomerException() {
    }

    public CustomerException(String message) {
        super(message);
    }

    public CustomerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerException(Throwable cause) {
        super(cause);
    }

    public CustomerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTree) {
        super(message, cause, enableSuppression, writableStackTree);
    }
}
