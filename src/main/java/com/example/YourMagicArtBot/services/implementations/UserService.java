package com.example.YourMagicArtBot.services.implementations;

import com.example.YourMagicArtBot.exceptions.IdNotFoundException;
import com.example.YourMagicArtBot.models.User;
import com.example.YourMagicArtBot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<User> findUserById(Long chatId) {
        return userRepository.findById(chatId);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> findAllUser() {
        List<User> userList = userRepository.findAll();

        if (userList.isEmpty())
            throw new IdNotFoundException("user database is empty");

        return userList;
    }

    public void saveUser(Message message) {
        userRepository.save(enrichUser(message));
    }


    private User enrichUser(Message message) {
        User user = new User();
        Chat chat = message.getChat();

        user.setDailyCounter(0);
        user.setWeeklyCounter(0);
        user.setUniversePredictionCounter(0);
        user.setUserName(chat.getUserName());
        user.setChatId(message.getChatId());
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

        return user;
    }

    public List<User> findUsersWhoDontUseTheirUniversePredictionAttempt() {
        return userRepository.findAllByUniversePredictionCounter(0);
    }

    public List<Long> getAllChatId() {
        List<Long> idList = userRepository.findAllChatId();

        if (idList.isEmpty())
            throw new IdNotFoundException("user database is empty");

        return idList;
    }
}
