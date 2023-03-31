package com.example.YourMagicArtBot.services;

import com.example.YourMagicArtBot.controllers.TelegramBot;
import com.example.YourMagicArtBot.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramApiService {

    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    public TelegramApiService(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public void init(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void executeVideo(SendVideo sendVideo) {
        try {
            telegramBot.execute(sendVideo);
        } catch (TelegramApiException e) {
            log.error("exception while video sending" + e.getMessage());
        }
    }

    public void executePhoto(SendPhoto sendPhoto) {
        try {
            telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error while sending photo message to user with chatId: " + sendPhoto.getChatId() + " cause: " + e.getMessage());
        }
    }

    public void executeMessage(EditMessageText editMessageText) {
        try {
            telegramBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Error while sending message to user with chatId: " + editMessageText.getChatId() + " cause: " + e.getMessage());
        }
    }

    public void executeMessage(SendMessage sendMessage) {
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message to user with chatId: " + sendMessage.getChatId() + " cause: " + e.getMessage());
        }
    }

    public void setMainKeyboardAndExecute(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(messageUtils.getMainKeyboard());
        executeMessage(sendMessage);
    }

    public void sendPhotoToUser(SendPhoto sendPhoto) {
        try {
            telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error while sending photo to user with chatId: " + sendPhoto.getChatId() + " cause: " + e.getMessage());
        }
    }
}
