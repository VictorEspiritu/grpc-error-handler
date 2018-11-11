package io.esev.grpc.server;

import io.esev.grpc.EchoRequest;
import io.esev.grpc.EchoResponse;
import io.esev.grpc.ErrorServiceGrpc;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ErrorServiceImpl extends ErrorServiceGrpc.ErrorServiceImplBase {

    private static final ExecutorService CANCELLATION_EXECUTOR = Executors.newCachedThreadPool();
    private static final int SECONDS_TO_WAIT = 5;

    @Override
    public void customUnwrapException(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        responseObserver.onError(new CustomerException());
    }

    @Override
    public void customException(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        try {
            throw new CustomerException("Custom Exception!");
        }catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).augmentDescription("customException()").withCause(e).asRuntimeException());
        }
    }

    @Override
    public void uncaughtException(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        throw new NullPointerException("uncaughtException(): Oops, not caught! What happes in the client?");
    }

    @Override
    public void deadLineExceeded(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        final Context context = Context.current();

        context.addListener(new Context.CancellationListener() {
            @Override
            public void cancelled(Context context) {
                System.out.println("[SERVICE] Exception deadLineExceeded(): The call was cancelled: " + context.cancellationCause());
            }
        }, CANCELLATION_EXECUTOR);

        context.run(() -> {
            int secondElapsed = 0;
            while (secondElapsed < SECONDS_TO_WAIT && !context.isCancelled()) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    System.out.println("[SERVICE] ERROR to Sleep: " + e.getMessage());
                }
                secondElapsed++;
            }
            System.out.println("[SERVICE] DeadLineExceeded(): ended in  " + secondElapsed + " seconds");
        });
    }

    @Override
    public void automaticallyWrappedException(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        responseObserver.onError(new IllegalArgumentException("This exception message and the stacktrace should automatically propagate to the client"));
    }
}
