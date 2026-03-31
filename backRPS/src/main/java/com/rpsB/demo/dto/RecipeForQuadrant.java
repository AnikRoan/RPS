package com.rpsB.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rpsB.demo.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeForQuadrant(
        Long uuid,
        String name,
        String description,
        List<IngredientFloatDto> ingredientFloatDtos,
        int timeToCookMinutes,
        int averageVote,
        Category category,
        Long userId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime created_at
) {
}
