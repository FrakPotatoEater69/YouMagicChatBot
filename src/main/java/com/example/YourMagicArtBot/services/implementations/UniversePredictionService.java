package com.example.YourMagicArtBot.services.implementations;

import com.example.YourMagicArtBot.models.UniversePrediction;
import com.example.YourMagicArtBot.models.User;
import com.example.YourMagicArtBot.repositories.UniversePredictionRepository;
import com.example.YourMagicArtBot.util.CountersUtils;
import com.example.YourMagicArtBot.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UniversePredictionService {
    private final UniversePredictionRepository universePredictionRepository;
    private final Random random = new Random();
    private final UserService userService;
    private final MessageUtils messageUtils;


    @Autowired
    public UniversePredictionService(UniversePredictionRepository universePredictionRepository, UserService userService, MessageUtils messageUtils) {
        this.universePredictionRepository = universePredictionRepository;
        this.userService = userService;
        this.messageUtils = messageUtils;
    }

    public void savePrediction(String textPredictionWithCommand) {
        String predictionText = textPredictionWithCommand.replaceAll("/sendUniverse ", "");
        UniversePrediction prediction = new UniversePrediction();
        prediction.setDescription(predictionText);

        universePredictionRepository.save(prediction);
    }

    public UniversePrediction getRandomPrediction() {

        List<Long> idsOfPredictions = universePredictionRepository.getAllId();
        Long randomId = idsOfPredictions.get(random.nextInt(idsOfPredictions.size()));

        return universePredictionRepository.findById(randomId).get();
    }

    public SendMessage processRequest(Message message) {
        String chatId = String.valueOf(message.getChatId());

        Optional<User> optionalUser = userService.findUserById(message.getChatId());

        if (optionalUser.isEmpty())
            return new SendMessage(chatId, "Вселенная тебя не узнаёт, отправь команду /start");

        if (CountersUtils.dayOfWeek == Calendar.THURSDAY) {
            User user = optionalUser.get();

            if (user.getUniversePredictionCounter() == 0) {

                SendMessage sendMessage = new SendMessage(chatId, "Задай себе вопрос, на который хочешь услышать ответ Вселенной");

                sendMessage.setReplyMarkup(messageUtils.getUniversePredictionKeyboard());

                return sendMessage;
            } else {
                return new SendMessage(chatId, "Ты уже получили ответ на твой вопрос на этой неделе.\n" +
                        "Задай свой вопрос в следующий четверг");
            }
        }

        return new SendMessage(chatId, messageUtils.UNIVERSE_GENERAL_INFO_OR_CLICK_NOT_THURSDAY);
    }

    public EditMessageText getEditedMessageWithPrediction(Update update) {
        EditMessageText message = new EditMessageText();
        if (CountersUtils.dayOfWeek == Calendar.THURSDAY) {
            String randomPrediction = getRandomPrediction().getDescription();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();


            message.setText(randomPrediction);
            message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            message.setChatId(update.getCallbackQuery().getMessage().getChatId());

            Optional<User> optionalUser = userService.findUserById(chatId);
            User user = optionalUser.get();
            user.setUniversePredictionCounter(1);
            userService.saveUser(user);
        } else {

            message.setText(messageUtils.TODAY_IS_NOT_THURSDAY);
            message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            message.setChatId(update.getCallbackQuery().getMessage().getChatId());
        }
        return message;
    }
}
