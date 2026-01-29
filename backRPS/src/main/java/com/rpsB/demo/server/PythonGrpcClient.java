package com.rpsB.demo.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PythonGrpcClient {
    private final ManagedChannel channel;
    private final infoToAI.grpc.EchoServiceGrpc.EchoServiceBlockingStub stub;

    public PythonGrpcClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        this.stub = infoToAI.grpc.EchoServiceGrpc.newBlockingStub(channel);
    }

    public String sendText(String text) {
        infoToAI.grpc.EchoRequest request = infoToAI.grpc.EchoRequest.newBuilder()
                .setText(text)
                .build();

        infoToAI.grpc.EchoReply reply = stub.echo(request);
        return reply.getText();
    }

    public List<String> getRecipeIds(String text) {
        infoToAI.grpc.EchoRequest request = infoToAI.grpc.EchoRequest.newBuilder()
                .setText(text)
                .build();

        infoToAI.grpc.RecipeListId response = stub.getRecipeIds(request);
        return response.getRecipeIdsList();

    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
