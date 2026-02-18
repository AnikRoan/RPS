package com.rpsB.demo.dto;

public record IngredientRequest(
        String name,
        Double amount,
        String unit,
        String note,
        Double calories,
        Double proteins,
        Double fats,
        Double carbs
) {
}
