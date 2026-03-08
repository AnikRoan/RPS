package com.rpsB.demo.dto;

import java.util.UUID;

public record IngredientFloatDto(
        Long id,
        UUID recipeId,
        String name
) {
}
