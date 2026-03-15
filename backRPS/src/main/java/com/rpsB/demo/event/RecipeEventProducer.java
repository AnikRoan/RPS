package com.rpsB.demo.event;

import com.rpsB.demo.dto.IngredientFloatDto;
import com.rpsB.demo.dto.RecipeForQuadrant;
import com.rpsB.demo.dto.RecipeShort;
import com.rpsB.demo.enums.SendStatus;
import com.rpsB.demo.repository.IngredientRepository;
import com.rpsB.demo.repository.RecipeRepository;
import com.rpsB.demo.service.RecipeBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeEventProducer {

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private final KafkaTemplate<String, List<RecipeForQuadrant>> kafkaTemplate;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeBatchService recipeBatchService;


    public void send() {

        List<UUID> lockedRecipeId = recipeBatchService.acquireBatch();

        if (lockedRecipeId.isEmpty()) {
            return;
        }

        List<RecipeShort> recipeShorts =
                recipeRepository.findByLockedUuids(lockedRecipeId);

        List<IngredientFloatDto> ingredientFloatDtos =
                ingredientRepository.findByRecipeId(lockedRecipeId);

        Map<UUID, List<IngredientFloatDto>> grouped =
                ingredientFloatDtos.stream()
                        .collect(Collectors.groupingBy(
                                IngredientFloatDto::recipeId));

        List<RecipeForQuadrant> result = recipeShorts.stream()
                .map(r -> new RecipeForQuadrant(
                        r.uuid(),
                        r.name(),
                        r.description(),
                        grouped.getOrDefault(r.uuid(), List.of()),
                        r.timeToCookMinutes(),
                        r.averageVote(),
                        r.category(),
                        r.userId(),
                        r.created_at()
                ))
                .toList();

        try {

            kafkaTemplate.send(topic, result)
                    .whenComplete((sendResult, ex) -> {

                        if (ex == null) {

                            log.info("Batch sent successfully");

                            recipeRepository.updateStatus(
                                    lockedRecipeId,
                                    SendStatus.SENT
                            );

                        } else {

                            log.error("Kafka send failed", ex);

                            recipeRepository.updateStatus(
                                    lockedRecipeId,
                                    SendStatus.PENDING
                            );
                        }
                    });

        } catch (Exception e) {

            log.error("Serialization failed", e);

            recipeRepository.updateStatus(
                    lockedRecipeId,
                    SendStatus.PENDING
            );
        }
    }


    @Scheduled(fixedDelay = 6000)
    public void sendEvent() {
        send();
    }

    @Scheduled(fixedDelay = 60000)
    private void recoverJob() {
        recipeBatchService.recoverStuckBatches();
    }
}
