package com.rpsB.demo.dto;

import com.rpsB.demo.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeResponse(
        Long uuid,
        String name,
        String description,
        List<IngredientResponse> ingredientResponseList,
        int timeToCookMinutes,
        int averageVote,
        Category category,
        Long userId,
        LocalDateTime created_at
) {}
