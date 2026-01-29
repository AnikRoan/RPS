package com.rpsB.demo.event;

import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.enums.SendStatus;
import com.rpsB.demo.mapper.RecipeMapper;
import com.rpsB.demo.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeEventProducer {

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    @Transactional
    public void sendPendingRecipes(){
//        List<RecipeResponse> resipeResponseList = recipeMapper.toDto(recipeRepository.findByStatusPending());
    }


    public void send(RecipeResponse dto) {
        kafkaTemplate.send(topic, dto.uuid().toString(), dto)
                .thenAccept(result -> {
                    log.info(result.getRecordMetadata().topic());

                })
                .exceptionally(ex -> {
                    log.info(ex.getMessage());
                    return null;

                });
    }


    @Transactional
    public void markSent(UUID recipeId) {
        recipeRepository.updateStaus(recipeId, SendStatus.SENT);
    }

    @Transactional
    public void markFailed(UUID recipeId) {
        recipeRepository.updateStaus(recipeId,SendStatus.PENDING);
    }
}
