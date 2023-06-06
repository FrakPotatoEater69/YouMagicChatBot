package com.example.YourMagicArtBot.util;

import com.example.YourMagicArtBot.models.Card;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageUtils {

    public static final String HELP = EmojiParser.parseToUnicode("The universe is kind and allows you to know your future through sacred cards. :stars:\n" +
            "\n" +
            "Once a day, but no more than 3 days a week, 3 attempts are refreshed every Sunday.\n" +
            "\n" +
            "Every Thursday the Universe is ready to answer one absolutely any question you have.:milky_way:\n" +
            "Also don't forget to get your gift from the Universe!");
    public static final String UNIVERSE_GENERAL_INFO_OR_CLICK_NOT_THURSDAY = EmojiParser.parseToUnicode(
            "Every week on Thursdays you can get answers from the universe :milky_way: to any question you have, but for now you just have to wait. \n" +
                    "She knows what you want."
    );
    public static final String CARD_NOT_FOUND = EmojiParser.parseToUnicode("For some reason, the Universe did not find the cards, a mess ...");
    public static final String CONNECTION = EmojiParser.parseToUnicode("Connecting with the Universe...:hourglass:");
    public static final String OK = "OK";
    public static final String NOT_OK = "NOT OK";
    public static final String USER_NOT_FOUND = "No users in the database";
    public static final String WAIT_AND_THINK = EmojiParser.parseToUnicode("Slow down :pray:, realize all the previous predictions and come on Sunday");

    public static final String UNIVERSE_PRESENT = EmojiParser.parseToUnicode("Here you can insert ads");
    public static final String READY_BUTTON = "READY_BUTTON";
    public static final String TODAY_IS_NOT_THURSDAY = "It's too late, the Universe only answers questions on Thursdays";
    public static final String SUPPORT = EmojiParser.parseToUnicode("If you find flaws or errors in the bot, please contact the developer \uD83D\uDC68\u200D\uD83D\uDCBB - @pressure_sensor\n" +
            "The bot is equipped with an anti-spam system according to the telegram rules and will not respond more than 1 message per second");

    public static final String UNSUPPORTED_MESSAGE_TEXT = EmojiParser.parseToUnicode("Use the menu or enter the /help command");

    public ReplyKeyboardMarkup getMainKeyboard() {
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow keyboardrow = new KeyboardRow();

        keyboardrow.add("Get a prediction");
        keyboardrow.add("Universe Answers");

        rows.add(keyboardrow);

        keyboardrow = new KeyboardRow();

        keyboardrow.add("Information");
        keyboardrow.add("Gift from the Universe");

        rows.add(keyboardrow);

        replyKeyboard.setResizeKeyboard(true);

        replyKeyboard.setKeyboard(rows);
        return replyKeyboard;
    }

    public ReplyKeyboard getUniversePredictionKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("I'm ready");
        button.setCallbackData(READY_BUTTON);

        rowInLine.add(button);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        return inlineKeyboardMarkup;
    }

    public static final String COME_TOMORROW = EmojiParser.parseToUnicode("The universe is accumulating cosmic energy. \uD83E\uDD0C \n" +
            "Come tomorrow");

    public SendMessage generateSendMessageByCard(Update update, Card card) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(card.getDescription());

        return sendMessage;
    }

    public SendMessage generateSendMessage(Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);

        return sendMessage;
    }

    public String getHelpMessage(String name) {
        return EmojiParser.parseToUnicode("Welcome message, here you can insert any text and any promotional video, but for example, I inserted a funny Polish video about a beaver");
    }

    public String getHelpMessageWithStartCommand(String name) {
        return EmojiParser.parseToUnicode("Help Message /start");
    }
}
