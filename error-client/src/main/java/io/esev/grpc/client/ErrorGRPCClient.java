package io.esev.grpc.client;

import io.esev.grpc.EchoRequest;
import io.esev.grpc.ErrorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class ErrorGRPCClient {

    public static void main(String[] args) {

        System.out.println("[CLIENT] Starting");
        ManagedChannel channel = ManagedChannelBuilder
                                    .forAddress("localhost", 9393)
                                    .usePlaintext(true)
                                    .build();
        final EchoRequest request = EchoRequest.getDefaultInstance();

        System.out.println("[CLIENT] Cresting Stub");
        ErrorServiceGrpc.ErrorServiceBlockingStub stub = ErrorServiceGrpc.newBlockingStub(channel);

        //Exception Deadline Exceeded
        try {
            System.out.println("[CLIENT] Wait DeadLine Exceeded....");
            stub.withDeadlineAfter(2, TimeUnit.SECONDS).deadLineExceeded(request);
        }catch (StatusRuntimeException e){
            if(e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED){
                System.out.println("[CLIENT] ERROR DeadLine Exceeded: " + e.getMessage());
            }
        }

        //Exception Not Implemented
        try{
            System.out.println("[CLIENT] Wait Not Implemented....");
            stub.notImplemented(request);
        }catch (StatusRuntimeException e){
            if(e.getStatus().getCode() == Status.Code.UNIMPLEMENTED) {
                System.out.println("[CLIENT] ERROR Unimplemented: " + e.getMessage());
            }
        }

        //Exception Uncaught Exception
        try{
            System.out.println("[CLIENT] Uncaught Exception....");
            stub.uncaughtException(request);
        }catch (StatusRuntimeException e){
            if(e.getStatus().getCode() == Status.Code.UNKNOWN) {
                System.out.println("[CLIENT] ERROR Uncaught Exception: " + e.getMessage());
            }
        }

        //Exception Custom Exception
        try{
            System.out.println("[CLIENT] Custom Exception....");
            stub.customException(request);
        }catch (StatusRuntimeException e){
            if(e.getStatus().getCode() == Status.Code.INTERNAL) {
                System.out.println("[CLIENT] ERROR Custom Exception: " + e.getMessage());
            }
        }

        //Exception Automatically Exception
        try{
            System.out.println("[CLIENT] Automatically Exception....");
            stub.automaticallyWrappedException(request);
        }catch (StatusRuntimeException e){
            if(e.getStatus().getCode() == Status.Code.INTERNAL) {
                System.out.println("[CLIENT] ERROR Automatically Exception: " + e.getMessage());
            }
        }

        channel.shutdown();

    }
}
