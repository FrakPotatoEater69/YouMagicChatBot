package com.example.YourMagicArtBot.repositories;

import com.example.YourMagicArtBot.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("select id from Card")
    List<Long> getAllId();

    @Query("select max (id) from Card")
    Long getMaxId();

    void deleteById(Long id);

}
