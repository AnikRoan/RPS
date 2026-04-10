package com.rpsB.demo.service;

import com.rpsB.demo.enums.SendStatus;
import com.rpsB.demo.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeBatchService {
    private final RecipeRepository recipeRepository;

    @Transactional
    public List<Long> acquireBatch() {

        List<Long> ids = recipeRepository.lockPendingIds();

        if (!ids.isEmpty()) {
            recipeRepository.updateStatus(ids, SendStatus.SENDING);
        }
        return ids;
    }

    @Transactional
    public void recoverStuckBatches() {
        int recovered = recipeRepository.recoverStuckSending();
        if (recovered > 0) {
            log.warn("Recovered {} stuck SENDING recipes", recovered);
        }
    }
}
