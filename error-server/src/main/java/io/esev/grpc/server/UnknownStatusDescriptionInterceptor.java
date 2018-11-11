package io.esev.grpc.server;

import io.grpc.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UnknownStatusDescriptionInterceptor implements ServerInterceptor {

    private final Set<Class<? extends Throwable>> autoWrapThrowables = new HashSet<>();

    public UnknownStatusDescriptionInterceptor(Collection<Class<? extends Throwable>> autoWrapThrowables) {
        this.autoWrapThrowables.addAll(autoWrapThrowables);
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {
            @Override
            public void sendMessage(RespT message) {
                System.out.println("[INTERCEPTOR] Error Handler SendMessage");
                super.sendMessage(message);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                System.out.println("[INTERCEPTOR] Error Handler Close");
                System.out.println("[INTERCEPTOR] Cause: "+ (status.getCause() == null ? "null" : status.getCause().getClass().getName()));

                if(status.getCode() == Status.Code.UNKNOWN
                    && status.getDescription() == null
                    && status.getCause() == null
                    && autoWrapThrowables.contains(status.getCause().getClass())) {

                    Throwable e = status.getCause();
                    status = Status.INTERNAL.withDescription(e.getMessage()).augmentDescription(stacktraceToString(e));
                }
                super.close(status, trailers);
            }
        };

        return serverCallHandler.startCall(wrappedCall,metadata);
    }

    private String stacktraceToString(Throwable e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        e.printStackTrace(printWriter);

        return stringWriter.toString();
    }
}
