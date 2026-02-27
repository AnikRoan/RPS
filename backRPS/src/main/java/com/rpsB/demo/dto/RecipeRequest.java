package com.rpsB.demo.dto;

import com.rpsB.demo.enums.Category;

import java.util.List;

public record RecipeRequest(
        String name,
        String description,
        List<IngredientRequest> ingredientDtoList,
        int timeToCookMinutes,
        Category category
) {
}
