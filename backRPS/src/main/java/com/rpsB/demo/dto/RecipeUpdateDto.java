package com.rpsB.demo.dto;

import com.rpsB.demo.enums.Category;

public record RecipeUpdateDto(
        String name,
        String description,
        int timeToCookMinutes,
        Category category
) {
}
