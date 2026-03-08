package com.rpsB.demo.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import recipe.RecipeSearch;
import recipe.RecipeSearchServiceGrpc;

import java.util.List;
import java.util.UUID;

@Service
public class PythonGrpcClient {
    private final ManagedChannel channel;
    private final RecipeSearchServiceGrpc.RecipeSearchServiceBlockingStub stub;

    public PythonGrpcClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        this.stub = RecipeSearchServiceGrpc.newBlockingStub(channel);
    }


    public List<UUID> getRecipeIds(String text) {
        RecipeSearch.SearchRequest request = RecipeSearch.SearchRequest.newBuilder()
                .setQuery(text)
                .build();

        RecipeSearch.SearchResponse reply = stub.searchRecipes(request);
        return reply.getRecipeIdsList()
                .stream()
                .map(UUID::fromString)
                .toList();
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
