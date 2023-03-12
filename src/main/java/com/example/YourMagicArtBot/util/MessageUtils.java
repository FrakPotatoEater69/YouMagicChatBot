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

    public static final String HELP = EmojiParser.parseToUnicode("Вселенная добра и позволяет узнать тебе твоё будущее через сакральные карты. :stars:\n" +
            "\n" +
            "Раз в день, но не больше трёх дней в неделю, 3 попытки обновляются каждое воскресенье.\n" +
            "\n" +
            "Каждый четверг Вселенная готова ответить на один абсолютно любой твой вопрос.:milky_way:\n" +
            "Так же не забудь получить свой подарок от Вселенной!");
    public static final String UNIVERSE_GENERAL_INFO_OR_CLICK_NOT_THURSDAY = EmojiParser.parseToUnicode(
            "Каждую неделю по четвергам ты сможешь получить ответы от вселенной:milky_way: на любой свой вопрос, а пока нужно просто ждать. \n" +
                    "Она знает чего ты хочешь."
    );
    public static final String CARD_NOT_FOUND = EmojiParser.parseToUnicode("Вселенная почему-то не нашла карт, непорядок...");
    public static final String CONNECTION = EmojiParser.parseToUnicode("Соединение со Вселенной...:hourglass:");
    public static final String OK = "OK";
    public static final String NOT_OK = "NOT OK";
    public static final String USER_NOT_FOUND = "Нет пользователей в БД";
    public static final String WAIT_AND_THINK = EmojiParser.parseToUnicode("Замедлись :pray:, осознай все предыдущие предсказания и приходи в воскресенье");

    public static final String UNIVERSE_PRESENT = EmojiParser.parseToUnicode("Привет, рада что ты с нами.\n" +
            "Я записала для тебя короткую практику, на высвобождение негативной энергии.\n" +
            "Это техника потокового рисования, которая отлично работает в моменте. Если понравиться, переходи на мой ютюб, в этой папке собраны похожие практики на разные ситуации\n" +
            "https://youtube.com/playlist?list=PL8I_kTo4KSLzdkNH10hPEI1dNj4-egteN\n" +
            "https://youtube.com/playlist?list=PL8I_kTo4KSLzF6bKChOMvrfgP-yBodV0O\n" +
            "\n" +
            "Если тебе интересно познакомиться поближе с сакральной геометрией или пройти один из моих курсов ты можешь выбрать подходящий тут\n" +
            "\n" +
            "https://taplink.cc/polina.davi\n" +
            "или написать мне в Инстаграме\n" +
            "https://instagram.com/polina.davi?igshid=YmMyMTA2M2Y= \n" +
            "на моем сайте ты найдешь подробную информацию обо мне и еще много интересного\n" +
            "https://polinadavi.com/");
    public static final String READY_BUTTON = "READY_BUTTON";
    public static final String TODAY_IS_NOT_THURSDAY = "Слишком поздно, Вселенная отвечает на вопросы только по четвергам";
    public static final String SUPPORT = EmojiParser.parseToUnicode("Если Вы нашли недоработки или ошибки в работе бота, пожалуйста, свяжитесь с разработчиком \uD83D\uDC68\u200D\uD83D\uDCBB - @pressure_sensor\n" +
            "Бот оснащён антиспам системой по правилам телеграма и не будет отвечать чаще чем на 1 сообщение в секунду");

    public ReplyKeyboardMarkup getMainKeyboard() {
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow keyboardrow = new KeyboardRow();

        keyboardrow.add("Получить предсказание");
        keyboardrow.add("Ответы Вселенной");

        rows.add(keyboardrow);

        keyboardrow = new KeyboardRow();

        keyboardrow.add("Информация");
        keyboardrow.add("Подарок от Вселенной");

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
        button.setText("Я готов");
        button.setCallbackData(READY_BUTTON);

        rowInLine.add(button);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        return inlineKeyboardMarkup;
    }

    public static final String COME_TOMORROW = EmojiParser.parseToUnicode("Вселенная копит космическую энергию. \uD83E\uDD0C \n" +
            "Приходи завтра");

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
        return EmojiParser.parseToUnicode("Привет, " + name + "!\n\n Я твой персональный магический бот.\n" +
                "\n" +
                "Меня создала художница и арт-терапевт Полина Дави.\n" +
                "\n" +
                "Я умею предсказывать ближайшее будущее с помощью сакральных символов, картин и метафорических карт\uD83D\uDD2E \n" +
                "\n" +
                "Хочешь получить первое предсказание? Жми кнопку Получить предсказание!");
    }

    public String getHelpMessageWithStartCommand(String name) {
        return EmojiParser.parseToUnicode("Привет, " + name + "!\n\n Я твой персональный магический бот.\n" +
                "\n" +
                "Меня создала художница и арт-терапевт Полина Дави.\n" +
                "\n" +
                "Я умею предсказывать ближайшее будущее с помощью сакральных символов, картин и метафорических карт\uD83D\uDD2E \n" +
                "\n" +
                "Хочешь получить первое предсказание? Отправь /start");
    }
}
