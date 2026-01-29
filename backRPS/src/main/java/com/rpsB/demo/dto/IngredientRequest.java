package com.rpsB.demo.dto;

public record IngredientRequest(
        String name,
        Double amount,
        String unit,
        String note,
        Integer position
) {
}
