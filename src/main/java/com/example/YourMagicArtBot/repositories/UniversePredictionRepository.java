package com.example.YourMagicArtBot.repositories;

import com.example.YourMagicArtBot.models.UniversePrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversePredictionRepository extends JpaRepository<UniversePrediction, Long> {

    @Query("select id from UniversePrediction")
    public List<Long> getAllId();
}
