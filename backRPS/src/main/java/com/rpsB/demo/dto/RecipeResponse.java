package com.rpsB.demo.dto;

import com.rpsB.demo.enums.Category;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        UUID uuid,
        String name,
        String description,
        List<IngredientResponse> ingredientResponseList,
        int timeToCookMinutes,
        int averageVote,
        Category category,
        Long userId,
        LocalDateTime created_at
) {}
