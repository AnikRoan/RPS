package com.rpsB.demo.repository;

import com.rpsB.demo.entity.Recipe;
import com.rpsB.demo.enums.SendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    //one queri give all objects with ids from the list
//    @Query("""
//            SELECT r FROM Recipe r
//            WHERE r.uuid = ANY(:uuids)
//            """)
//    List<Recipe> findByUuidIn(@Param("uuids") UUID[] uuids);
    @Query(value = "SELECT * FROM recipes WHERE uuid = ANY(:uuids)", nativeQuery = true)
    List<Recipe> findByUuids(@Param("uuids") UUID[] uuids);

//    in code repository.findByUuidIn(ids.toArray(UUID[]::new));



    @Query("""
            SELECT r FROM Recipe  r
            WHERE r.status = 'PENDING'            
            """)
    List<Recipe> findByStatusPending();

    @Modifying
    @Query("""
           UPDATE Recipe r 
           SET r.status = :status
           WHERE r.uuid = :uuid                               
            """)
    int updateStaus(@Param("uuid")UUID uuid,
                    @Param("status")SendStatus status);
}

//@Query("""
//    SELECT new com.rpsB.demo.dto.RecipeCardDto(
//        r.uuid,
//        r.name,
//        r.timeToCookMinutes,
//        r.averageVote
//    )
//    FROM Recipe r
//    WHERE r.uuid IN :ids
//""")
//List<RecipeCardDto> findRecipeCardsByIds(@Param("ids") List<UUID> ids);
