package com.rpsB.demo.dto;

public record IngredientDto(
        Long id,
        Long recipe_id,
        String name,
        Double amount,
        String unit,
        String note,
        Integer position
) {
}
