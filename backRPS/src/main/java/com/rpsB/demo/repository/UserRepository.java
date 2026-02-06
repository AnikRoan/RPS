package com.rpsB.demo.repository;

import com.rpsB.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("""
            SELECT u.email FROM User u
            WHERE u.email = :email            
            """)
    Optional<String> findEmail(String email);

    @Query("""
            SELECT u FROM User  u 
            WHERE u.email =:email            
            """)
    Optional<User> findByEmail(String email);
}
