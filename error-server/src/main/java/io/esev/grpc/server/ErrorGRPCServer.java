package io.esev.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;
import java.util.Arrays;

public class ErrorGRPCServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("[SERVER] Starting Server");
        UnknownStatusDescriptionInterceptor interceptor = new UnknownStatusDescriptionInterceptor(Arrays.asList(IllegalArgumentException.class));

        System.out.println("[SERVER] Server build");
        Server server = ServerBuilder
                            .forPort(9393)
                            .addService(ServerInterceptors.intercept(new ErrorServiceImpl(), interceptor))
                            .build();

        server.start();
        System.out.println("[SERVER] Server Started");
        server.awaitTermination();
    }
}
