package com.example.YourMagicArtBot.controllers;

import com.example.YourMagicArtBot.exceptions.CardNotFoundException;
import com.example.YourMagicArtBot.exceptions.IdNotFoundException;
import com.example.YourMagicArtBot.models.Card;
import com.example.YourMagicArtBot.models.User;
import com.example.YourMagicArtBot.services.CardService;
import com.example.YourMagicArtBot.services.TelegramApiService;
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
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class UpdateController {
    private final UserService userService;
    private TelegramBot telegramBot;
    private final AdminValidator adminValidator;
    private final MessageUtils messageUtils;
    private final CardService cardService;
    private final UniversePredictionService universePredictionService;
    private final VideoUtils videoUtils;
    final TelegramApiService telegramApiService;

    public UpdateController(UserService userService, AdminValidator adminValidator, MessageUtils messageUtils, CardService cardService, UniversePredictionService universePredictionService, VideoUtils videoUtils, TelegramApiService telegramApiService) {
        this.userService = userService;
        this.adminValidator = adminValidator;
        this.messageUtils = messageUtils;
        this.cardService = cardService;
        this.universePredictionService = universePredictionService;
        this.videoUtils = videoUtils;
        this.telegramApiService = telegramApiService;
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
            log.error("Bot get massage not from user chat");
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
            case ("READY_BUTTON") -> telegramApiService.executeMessage(universePredictionService.
                    getEditedMessageWithPrediction(update));
        }
    }

    private void processMessage(Update update) {
        if (update.getMessage().hasText()) {
            processTextMessage(update);
        } else if (update.getMessage().hasPhoto()) {
            processPhotoMessage(update);
        } else if (update.getMessage().hasVideo()) {
            if (adminValidator.isOwner(update.getMessage().getChatId())) {
                log.info("Video with fileId: " + update.getMessage().getVideo().getFileId() + " was received");
            }
        }
    }

    private void unsupportedMessageReceived(Update update) {
        telegramApiService.setMainKeyboardAndExecute(messageUtils.
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
                telegramApiService.setMainKeyboardAndExecute(messageUtils.
                        generateSendMessage(update, "Card added successfully!"));
            } else {
                telegramApiService.setMainKeyboardAndExecute(messageUtils.
                        generateSendMessage(update, "To add a new card, please send an image and a caption (description) in one message"));
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
            telegramApiService.executePhoto(sendPhoto);
            counter++;
        }

        telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, "OK, " + counter + " users received photo"));
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
                case "Get a prediction" -> dailyPredictionRequestReceived(update);
                case "Universe Answers" -> universePredictionRequestReceived(update);
                case "Information" -> infoRequestReceived(update);
                case "Gift from the Universe" -> universePresentRequestReceived(update);
                case "/start" -> registerUser(update);
                case "/help" -> helpRequestReceived(update);
                case "/deleteLastCard" -> deleteLastCardRequestReceived(update);
                case "/support" -> supportRequestReceived(update);

                default -> unsupportedMessageReceived(update);
            }
        }
    }

    private void supportRequestReceived(Update update) {
        telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, messageUtils.SUPPORT));
    }

    private void universePresentRequestReceived(Update update) {
        telegramApiService.executeVideo(videoUtils.getInfoVideo(update));
    }

    private void mailingRequestReceived(Update update) {
        if (adminValidator.isOwner(update.getMessage().getChatId())) {
            String mailingText = update.getMessage().getText().replace("/sendAll ", "");

            List<User> userList = null;
            try {
                userList = userService.findAllUser();
            } catch (IdNotFoundException e) {
                telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, messageUtils.USER_NOT_FOUND));
                return;
            }

            int counter = doMassMailingAndCountIt(userList, mailingText);
            telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, messageUtils.OK + " " + counter));
            return;
        }
        unsupportedMessageReceived(update);
    }

    private int doMassMailingAndCountIt(List<User> userList, String mailingText) {
        int counter = 0;

        if (mailingText.contains("{NAME}")) {
            for (User user : userList) {
                String personalMailingText = mailingText.replace("{NAME}", user.getFirstName());
                telegramApiService.setMainKeyboardAndExecute(new SendMessage(String.valueOf(user.getChatId()), personalMailingText));
                counter++;
            }
        } else {
            for (User user : userList) {
                telegramApiService.setMainKeyboardAndExecute(new SendMessage(String.valueOf(user.getChatId()), mailingText));
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
                log.error("Trying to remove a map from an empty database" + e.getMessage());
                telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.NOT_OK));
                return;
            }

            telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.OK));
        } else
            unsupportedMessageReceived(update);
    }

    private void helpRequestReceived(Update update) {
        infoRequestReceived(update);
    }

    private void infoRequestReceived(Update update) {
        telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.HELP));
    }

    private void universePredictionRequestReceived(Update update) {
        telegramApiService.executeMessage(universePredictionService.processRequest(update.getMessage()));
    }

    private void newUniversePredictionReceived(Update update) {
        if (adminValidator.isOwner(update.getMessage().getChatId())) {
            universePredictionService.savePrediction(update.getMessage().getText());
            telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.OK));
        }

        unsupportedMessageReceived(update);
    }

    private void dailyPredictionRequestReceived(Update update) {
        Optional<User> optionalUser = userService.findUserById(update.getMessage().getChatId());
        if (optionalUser.isEmpty()) {
            telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update,
                    messageUtils.getHelpMessageWithStartCommand(update.getMessage().getChat().getFirstName())));
        } else if (optionalUser.get().getWeeklyCounter() >= 3) {
            telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update,
                    messageUtils.WAIT_AND_THINK));
        } else if (optionalUser.get().getDailyCounter() == 1) {
            telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update,
                    messageUtils.COME_TOMORROW));
        } else {
            sendRandomPrediction(update, optionalUser);
        }
    }

    private void sendRandomPrediction(Update update, Optional<User> optionalUser) {
        User user = optionalUser.get();

        telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.CONNECTION));

        Card randomCard = null;
        try {
            randomCard = cardService.getRandomCard();
        } catch (CardNotFoundException e) {
            log.error("Random card not found" + e.getMessage());
            telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessage(update, MessageUtils.CARD_NOT_FOUND));
            return;
        }

        user.setDailyCounter(1);
        user.setWeeklyCounter(user.getWeeklyCounter() + 1);
        userService.saveUser(user);

        telegramApiService.sendPhotoToUser(cardService.generateSendPhotoByCard(update, randomCard));
        telegramApiService.setMainKeyboardAndExecute(messageUtils.generateSendMessageByCard(update, randomCard));
    }

    private void registerUser(Update update) {
        Message message = update.getMessage();
        if (userService.findUserById(message.getChatId()).isEmpty()) {
            userService.saveUser(message);

            telegramApiService.setMainKeyboardAndExecute(messageUtils.
                    generateSendMessage(update, messageUtils.getHelpMessage(update.getMessage().getChat().getFirstName())));

            telegramApiService.executeVideo(videoUtils.getWelcomeVideo(update));
        } else {
            telegramApiService.setMainKeyboardAndExecute(messageUtils.
                    generateSendMessage(update, EmojiParser.
                            parseToUnicode("There is no need, the Universe remembers everything! :dizzy:")));
        }
    }


}
