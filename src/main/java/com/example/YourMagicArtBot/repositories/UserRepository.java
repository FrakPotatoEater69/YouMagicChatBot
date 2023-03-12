package com.example.YourMagicArtBot.repositories;

import com.example.YourMagicArtBot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByUniversePredictionCounter(int universePredictionCounter);

    @Query("select chatId from User")
    List<Long> findAllChatId();
}
