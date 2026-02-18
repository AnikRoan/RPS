package com.rpsB.demo.mapper;

import com.rpsB.demo.dto.IngredientResponse;
import com.rpsB.demo.dto.IngredientRequest;
import com.rpsB.demo.entity.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    IngredientResponse toDto(Ingredient ingredient);

    Ingredient toEntity(IngredientRequest dto);
}
