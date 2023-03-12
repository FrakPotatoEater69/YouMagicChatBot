package com.example.YourMagicArtBot.controllers;

import com.example.YourMagicArtBot.exceptions.CardNotFoundException;
import com.example.YourMagicArtBot.exceptions.IdNotFoundException;
import com.example.YourMagicArtBot.models.Card;
import com.example.YourMagicArtBot.models.User;
import com.example.YourMagicArtBot.services.CardServiceImpl;
import com.example.YourMagicArtBot.services.UniversePredictionService;
import com.example.YourMagicArtBot.services.UserService;
import com.example.YourMagicArtBot.util.AdminValidator;
import com.example.YourMagicArtBot.util.MessageUtils;
import com.example.YourMagicArtBot.util.RateLimiter;
import com.example.YourMagicArtBot.util.VideoUtils;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class UpdateController {
    private final UserService userService;
    private TelegramBot telegramBot;
    private final AdminValidator adminValidator;
    private final MessageUtils messageUtils;
    private final CardServiceImpl cardService;
    private final UniversePredictionService universePredictionService;
    private final VideoUtils videoUtils;

    public UpdateController(UserService userService, AdminValidator adminValidator, MessageUtils messageUtils, CardServiceImpl cardService, UniversePredictionService universePredictionService, VideoUtils videoUtils) {
        this.userService = userService;
        this.adminValidator = adminValidator;
        this.messageUtils = messageUtils;
        this.cardService = cardService;
        this.universePredictionService = universePredictionService;
        this.videoUtils = videoUtils;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }
        if (update.hasCallbackQuery()) {
            processCallBackQuery(update);
            return;
        }
        if (update.getMessage() == null) {
            log.error("Received message is null");
            return;
        }
        if (!update.getMessage().isUserMessage()) {
            log.warn("Bot get massage not from user chat");
            return;
        }
        if (!RateLimiter.isRequestAllowed(String.valueOf(update.getMessage().getChatId()))) {
            log.warn("User " + update.getMessage().getChat().getFirstName() + " with chatId: " + update.getMessage().getChatId() + " is spamming!");
        } else {
            processMessage(update);
        }
    }

    private void processCallBackQuery(Update update) {
        switch (update.getCallbackQuery().getData()) {
            case ("READY_BUTTON") -> executeMessage(universePredictionService.
                    getEditedMessageWithPrediction(update));
        }
    }

    private void processMessage(Update update) {
        if (update.getMessage().hasText()) {
            processTextMessage(update);
        } else if (update.getMessage().hasPhoto()) {
            processPhotoMessage(update);
        } else if (update.getMessage().hasVideo()) {
            if(adminValidator.isOwner(update.getMessage().getChatId())) {
                log.info("Video with fileId: " + update.getMessage().getVideo().getFileId() + " was received");
            }
        }
    }

    private void unsupportedMessageReceived(Update update) {
        setMainKeyboardAndExecute(messageUtils.
                generateSendMessage(update, "Воспользуйся меню или введи команду /help"));
    }

    private void processPhotoMessage(Update update) {
        if (adminValidator.isOwner(update.getMessage().getChatId())) {
            if (update.getMessage().getCaption() != null) {
                if (update.getMessage().getCaption().startsWith("/sendAll")) {
                    mailingRequestWithPhotoReceived(update);
                    return;
                }
                cardService.processPhoto(update.getMessage());
                setMainKeyboardAndExecute(messageUtils.
                        generateSendMessage(update, "Карта успешно добавлена!"));
            } else {
                setMainKeyboardAndExecute(messageUtils.
                        generateSendMessage(update, "Чтобы добавить новую карту, пожалуйста, отправьте одним сообщением изображение и подпись (описание)"));
            }
        } else {
            unsupportedMessageReceived(update);
        }

    }

    private void mailingRequestWithPhotoReceived(Update update) {
        log.info("Mailing PhotoMessage begin");
        String photoId = update.getMessage().getPhoto().get(update.getMessage().getPhoto().size() - 1).getFileId();
        List<User> userList = userService.findAllUser();

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(update.getMessage().getCaption().replace("/sendAll ", ""));
        sendPhoto.setPhoto(new InputFile(photoId));
        sendPhoto.setReplyMarkup(messageUtils.getMainKeyboard());

        int counter = 0;

        for (User user : userList) {
            sendPhoto.setChatId(user.getChatId());
            executePhoto(sendPhoto);
            counter++;
        }

        setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, "OK, " + counter + " users received photo"));
        log.info("Mailing PhotoMessage end");
    }

    private void processTextMessage(Update update) {
        String messageTest = update.getMessage().getText();
        if (update.getMessage().getText().startsWith("/sendUniverse")) {
            newUniversePredictionReceived(update);
        } else if (update.getMessage().getText().startsWith("/sendAll")) {
            mailingRequestReceived(update);
        } else {
            switch (messageTest) {
                case "Получить предсказание" -> dailyPredictionRequestReceived(update);
                case "Ответы Вселенной" -> universePredictionRequestReceived(update);
                case "Информация" -> infoRequestReceived(update);
                case "Подарок от Вселенной" -> universePresentRequestReceived(update);
                case "/start" -> registerUser(update);
                case "/help" -> helpRequestReceived(update);
                case "/deleteLastCard" -> deleteLastCardRequestReceived(update);
                case "/support" -> supportRequestReceived(update);

                default -> unsupportedMessageReceived(update);
            }
        }
    }

    private void supportRequestReceived(Update update) {
        setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, messageUtils.SUPPORT));
    }

    private void universePresentRequestReceived(Update update) {
        executeVideo(videoUtils.getInfoVideo(update));
    }

    private void mailingRequestReceived(Update update) {
        if (adminValidator.isOwner(update.getMessage().getChatId())) {
            String mailingText = update.getMessage().getText().replace("/sendAll ", "");

            List<User> userList = null;
            try {
                userList = userService.findAllUser();
            } catch (IdNotFoundException e) {
                setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, messageUtils.USER_NOT_FOUND));
                return;
            }

            int counter = doMassMailingAndCountIt(userList, mailingText);
            setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, messageUtils.OK + " " + counter));
            return;
        }
        unsupportedMessageReceived(update);
    }

    private int doMassMailingAndCountIt(List<User> userList, String mailingText) {
        int counter = 0;

        if (mailingText.contains("{NAME}")) {
            for (User user : userList) {
                String personalMailingText = mailingText.replace("{NAME}", user.getFirstName());
                setMainKeyboardAndExecute(new SendMessage(String.valueOf(user.getChatId()), personalMailingText));
                counter++;
            }
        } else {
            for (User user : userList) {
                setMainKeyboardAndExecute(new SendMessage(String.valueOf(user.getChatId()), mailingText));
                counter++;
            }
        }

        return counter;
    }

    private void deleteLastCardRequestReceived(Update update) {
        if (adminValidator.isOwner(update.getMessage().getChatId())) {
            try {
                cardService.deleteLastAddedCard();
            } catch (CardNotFoundException e) {
                log.error("Попытка удалить карту с пустой бд" + e.getMessage());
                setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.NOT_OK));
                return;
            }

            setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.OK));
        } else
            unsupportedMessageReceived(update);
    }

    private void helpRequestReceived(Update update) {
        infoRequestReceived(update);
    }

    private void infoRequestReceived(Update update) {
        setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.HELP));
    }

    private void universePredictionRequestReceived(Update update) {
        executeMessage(universePredictionService.processRequest(update.getMessage()));
    }

    private void newUniversePredictionReceived(Update update) {
        if (adminValidator.isOwner(update.getMessage().getChatId())) {
            universePredictionService.savePrediction(update.getMessage().getText());
            setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.OK));
        }

        unsupportedMessageReceived(update);
    }

    private void dailyPredictionRequestReceived(Update update) {
        Optional<User> optionalUser = userService.findUserById(update.getMessage().getChatId());
        if (optionalUser.isEmpty()) {
            setMainKeyboardAndExecute(messageUtils.generateSendMessage(update,
                    messageUtils.getHelpMessageWithStartCommand(update.getMessage().getChat().getFirstName())));
        } else if (optionalUser.get().getWeeklyCounter() >= 3) {
            setMainKeyboardAndExecute(messageUtils.generateSendMessage(update,
                    messageUtils.WAIT_AND_THINK));
        } else if (optionalUser.get().getDailyCounter() == 1) {
            setMainKeyboardAndExecute(messageUtils.generateSendMessage(update,
                    messageUtils.COME_TOMORROW));
        } else {
            sendRandomPrediction(update, optionalUser);
        }
    }

    private void sendRandomPrediction(Update update, Optional<User> optionalUser) {
        User user = optionalUser.get();

        setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.CONNECTION));

        Card randomCard = null;
        try {
            randomCard = cardService.getRandomCard();
        } catch (CardNotFoundException e) {
            log.error("Random card not found" + e.getMessage());
            setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.CARD_NOT_FOUND));
            return;
        }

        user.setDailyCounter(1);
        user.setWeeklyCounter(user.getWeeklyCounter() + 1);
        userService.saveUser(user);

        sendPhotoToUser(cardService.generateSendPhotoByCard(update, randomCard));
        setMainKeyboardAndExecute(messageUtils.generateSendMessageByCard(update, randomCard));
    }

    private void sendPhotoToUser(SendPhoto sendPhoto) {
        try {
            telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMainKeyboardAndExecute(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(messageUtils.getMainKeyboard());
        executeMessage(sendMessage);
    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message to user with chatId: " + sendMessage.getChatId() + " cause: " + e.getMessage());
        }
    }

    private void executeMessage(EditMessageText editMessageText) {
        try {
            telegramBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Error while sending message to user with chatId: " + editMessageText.getChatId() + " cause: " + e.getMessage());
        }
    }

    private void executePhoto(SendPhoto sendPhoto) {
        try {
            telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error while sending photo message to user with chatId: " + sendPhoto.getChatId() + " cause: " + e.getMessage());
        }
    }

    private void executeVideo(SendVideo sendVideo){
        try {
            telegramBot.execute(sendVideo);
        } catch (TelegramApiException e) {
            log.error("exception while video sending" + e.getMessage());
        }
    }

    private void registerUser(Update update) {
        Message message = update.getMessage();
        if (userService.findUserById(message.getChatId()).isEmpty()) {
            userService.saveUser(message);

            setMainKeyboardAndExecute(messageUtils.
                    generateSendMessage(update, messageUtils.getHelpMessage(update.getMessage().getChat().getFirstName())));

            executeVideo(videoUtils.getWelcomeVideo(update));
        } else {
            setMainKeyboardAndExecute(messageUtils.
                    generateSendMessage(update, EmojiParser.
                            parseToUnicode("Нет нужды, Вселенная всё помнит! :dizzy:")));
        }
    }


}
