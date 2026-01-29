package com.rpsB.demo.dto;

import java.util.List;

public record RecipeRequest(
        String name,
        String description,
        List<IngredientRequest> ingredientDtoList,
        int timeToCookMinutes
) {
}
