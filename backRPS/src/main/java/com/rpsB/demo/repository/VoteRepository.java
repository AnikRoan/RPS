package com.rpsB.demo.repository;

import com.rpsB.demo.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("""
            SELECT v FROM Vote v 
            WHERE v.recipe.uuid = :recipe_id            
            """)
    Page<Vote> findVotesByRecipeId(@Param("recipe_id") UUID recipe_Id, Pageable pageable);
}
