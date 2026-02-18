package com.rpsB.demo.dto;

public record IngredientResponse(
        Long id,
        Long recipe_id,
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
