package com.rpsB.demo.mapper;

import com.rpsB.demo.dto.RecipeRequest;
import com.rpsB.demo.dto.RecipeResponse;
import com.rpsB.demo.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {IngredientMapper.class}
)
public interface RecipeMapper {

    //Entity -> Response dto
    @Mapping(source = "ingredientList", target = "ingredientResponseList")
    @Mapping(source = "creator.id", target = "userId")
    RecipeResponse toDto(Recipe recipe);

    @Mapping(source = "recipe.ingredientList", target = "ingredientResponseList")
    @Mapping(source = "userId", target = "userId")
    RecipeResponse toDtoWithId(Recipe recipe, Long userId);

    //Request dto -> Entity
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "averageVote", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    @Mapping(source = "ingredientDtoList", target = "ingredientList")
    Recipe toEntity(RecipeRequest request);

    List<RecipeResponse> toDtos(List<Recipe> recipes);
}
