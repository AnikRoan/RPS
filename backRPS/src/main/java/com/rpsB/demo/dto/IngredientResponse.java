package com.rpsB.demo.dto;

import java.util.UUID;

public record IngredientResponse(
        Long id,
        Long recipeId,
        String name,
        Double amount,
        String unit,
        String note,
        Integer position,
        Double calories,
        Double proteins,
        Double fats,
        Double carbs
) {
}
