package com.example.YourMagicArtBot.util;

import com.example.YourMagicArtBot.controllers.TelegramBot;
import com.example.YourMagicArtBot.models.User;
import com.example.YourMagicArtBot.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.List;

@Component
@Slf4j
public class CountersUtils {
    private final UserService userService;
    public static int dayOfWeek;
    private final TelegramBot telegramBot;


    public CountersUtils(UserService userService, TelegramBot telegramBot) {
        this.userService = userService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    private void setRebootDayOfWeekCounter() {
        Calendar calendar = Calendar.getInstance();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        log.info("reboot resetting. Current day of the week:" + dayOfWeek);
    }

    @Scheduled(cron = "10 0 0 * * *")
    private void setNewDayOfWeekCounter() {
        Calendar calendar = Calendar.getInstance();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        log.info("midnight resetting. Current day of the week: " + dayOfWeek);
    }


    @Scheduled(cron = "15 0 0 * * *")
    private void resetDailyPredictionCounter() {
        List<User> userList = userService.findAllUser();
        if (userList.isEmpty()) {
            log.info("userList is empty");
        } else {
            for (User user : userList) {
                user.setDailyCounter(0);
                userService.saveUser(user);
                log.info("Daily prediction counter resetting...");
            }
        }
    }

    @Scheduled(cron = "20 0 0 * * SUN")
    private void resetWeeklyPredictionCounter() {
        List<User> userList = userService.findAllUser();
        if (userList.isEmpty()) {
            log.info("userList is empty");
        } else {
            for (User user : userList) {
                user.setWeeklyCounter(0);
                userService.saveUser(user);
                log.info("Weekly prediction counter resetting...");
            }
        }
    }

    @Scheduled(cron = "40 0 0 * * THU")
    private void resetUniversePredictionCounter() {
        List<User> userList = userService.findAllUser();
        if (userList.isEmpty()) {
            log.info("userList is empty");
        } else {
            log.info("Universe prediction counter resetting...");
            for (User user : userList) {
                user.setUniversePredictionCounter(0);
                userService.saveUser(user);
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * THU")
    private void sendReminderToUsersWhoDidNotUseUniversePredictionAttempt() {
        log.info("beginning of reminders");
        List<User> userList = userService.findUsersWhoDontUseTheirUniversePredictionAttempt();
        if (userList.isEmpty())
            return;
        for (User user : userList) {
            try {
                log.info("Sending reminders to: " + user.getUserName());
                SendMessage sendMessage = new SendMessage(String.valueOf(user.getChatId()),
                        "Сегодня тот самый день, когда Вселенная готова ответить на твой вопрос.\n" +
                                "Задай себе вопрос и нажми на кнопку Ответы вселенной");
                telegramBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error while sending reminders to: " + user.getUserName() + " with chatId: " + user.getChatId() + " cause: " + e.getMessage());
            }
        }
        log.info("end of reminders");
    }

    @Scheduled(cron = "0 0 9 * * SUN")
    private void sendSundayReminderAboutWeeklyPredictionCounterReset() {
        log.info("beginning sending of sunday reminders");
        List<User> userList = userService.findAllUser();
        if (userList.isEmpty())
            return;
        for (User user : userList) {
            try {
                log.info("Sending reminders to: " + user.getUserName());
                SendMessage sendMessage = new SendMessage(String.valueOf(user.getChatId()),
                        "Привет " + user.getFirstName() + " Вселенная о тебе заботится и готова к новой неделе.\n" +
                                "Уже сегодня произошло обновление, можешь начать запрашивать!");
                telegramBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error while sending reminders to: " + user.getUserName() + " with chatId: " + user.getChatId() + " cause: " + e.getMessage());
            }
        }
        log.info("end of reminders");
    }

}

