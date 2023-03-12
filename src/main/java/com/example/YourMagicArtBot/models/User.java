package com.example.YourMagicArtBot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.List;

@Entity()
@Table(name = "user_data")
public class User {
    @Id
    private Long chatId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String userName;

    @Column
    private Timestamp registeredAt;

    @Column
    private Integer dailyCounter;

    @Column
    private Integer weeklyCounter;

    @Column
    private Integer universePredictionCounter;

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Integer getDailyCounter() {
        return dailyCounter;
    }

    public void setDailyCounter(Integer dailyCounter) {
        this.dailyCounter = dailyCounter;
    }

    public Integer getWeeklyCounter() {
        return weeklyCounter;
    }

    public void setWeeklyCounter(Integer weeklyCounter) {
        this.weeklyCounter = weeklyCounter;
    }

    public Integer getUniversePredictionCounter() {
        return universePredictionCounter;
    }

    public void setUniversePredictionCounter(Integer universePredictionCounter) {
        this.universePredictionCounter = universePredictionCounter;
    }
}
