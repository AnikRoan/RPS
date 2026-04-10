package com.rpsB.demo.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import recipe.RecipeSearch;
import recipe.RecipeSearchServiceGrpc;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PythonGrpcClient {
    private final ManagedChannel channel;
    private final RecipeSearchServiceGrpc.RecipeSearchServiceBlockingStub stub;

    public PythonGrpcClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        this.stub = RecipeSearchServiceGrpc
                .newBlockingStub(channel)
                .withDeadlineAfter(40, TimeUnit.SECONDS);
    }


    public List<Long> getRecipeIds(String text) {
        log.info(text);
        log.info("start search");
        RecipeSearch.SearchRequest request = RecipeSearch.SearchRequest.newBuilder()
                .setQuery(text)
                .build();
        log.info("REQUEST: {} ",request.getQuery());
        RecipeSearch.SearchResponse reply = stub.searchRecipes(request);
        log.info("Response received. Recipes found: {}", reply.getRecipeIdsCount());

        return reply.getRecipeIdsList();
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
