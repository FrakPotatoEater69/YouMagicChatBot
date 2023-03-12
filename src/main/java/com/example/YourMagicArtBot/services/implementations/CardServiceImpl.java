package com.example.YourMagicArtBot.services.implementations;

import com.example.YourMagicArtBot.exceptions.CardNotFoundException;
import com.example.YourMagicArtBot.exceptions.UploadFileException;
import com.example.YourMagicArtBot.models.Card;
import com.example.YourMagicArtBot.repositories.CardRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

@Service
public class CardServiceImpl {
    @Value("${bot.token}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final CardRepository cardRepository;

    private final Random random = new Random();


    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card processPhoto(Message telegramMessage) {
        int photoSizeCount = telegramMessage.getPhoto().size();
        int photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;

        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);

        String fileId = telegramPhoto.getFileId();
        String caption = telegramMessage.getCaption();
        ResponseEntity<String> response = getFilePath(fileId);

        if (response.getStatusCode() == HttpStatus.OK) {

            Card card = getReadyToSaveCard(response, caption);

            return cardRepository.save(card);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private Card getReadyToSaveCard(ResponseEntity<String> response, String caption) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);

        Card card = new Card();
        card.setImageOfCardAsAnArray(fileInByte);
        card.setDescription(caption);

        return card;
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e.getMessage());
        }

        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(e.getMessage());
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    public Card getRandomCard() {

        List<Long> idsOfCards = cardRepository.getAllId();

        if (idsOfCards.isEmpty())
            throw new CardNotFoundException();

        Long randomId = idsOfCards.get(random.nextInt(idsOfCards.size()));


        return cardRepository.findById(randomId).get();
    }

    public SendPhoto generateSendPhotoByCard(Update update, Card randomCard) {

        InputStream inputStream = new ByteArrayInputStream(randomCard.getImageOfCardAsAnArray());
        InputFile inputFile = new InputFile(inputStream, "rnd");
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(update.getMessage().getChatId()), inputFile);
        return sendPhoto;
    }

    public void deleteLastAddedCard() {
        Long maxId = cardRepository.getMaxId();

        if (maxId == null)
            throw new CardNotFoundException();

        cardRepository.deleteById(maxId);
    }
}
