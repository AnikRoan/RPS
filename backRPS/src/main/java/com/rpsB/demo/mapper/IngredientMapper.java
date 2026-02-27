package com.rpsB.demo.mapper;

import com.rpsB.demo.dto.IngredientResponse;
import com.rpsB.demo.dto.IngredientRequest;
import com.rpsB.demo.entity.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    @Mapping(source = "recipe.uuid", target = "recipeId")
    IngredientResponse toDto(Ingredient ingredient);

    Ingredient toEntity(IngredientRequest dto);
}
