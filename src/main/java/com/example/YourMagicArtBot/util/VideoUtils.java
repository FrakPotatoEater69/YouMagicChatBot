package com.example.YourMagicArtBot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class VideoUtils {

    private final MessageUtils messageUtils;
    @Value("${welcomeVideoFileId}")
    private String WELCOME_VIDEO_FILE_ID;

    @Value("${infoVideoFileId}")
    private String INFO_VIDEO_FILE_ID;

    public VideoUtils(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public SendVideo getWelcomeVideo(Update update) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(update.getMessage().getChatId());
        sendVideo.setVideo(new InputFile(WELCOME_VIDEO_FILE_ID));

        return sendVideo;
    }

    public SendVideo getInfoVideo(Update update) {
        SendVideo sendVideo = new SendVideo();

        sendVideo.setChatId(update.getMessage().getChatId());
        sendVideo.setVideo(new InputFile(INFO_VIDEO_FILE_ID));
        sendVideo.setCaption(messageUtils.UNIVERSE_PRESENT);


        return sendVideo;
    }
}
