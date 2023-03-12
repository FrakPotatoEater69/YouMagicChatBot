package com.example.YourMagicArtBot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CardImage")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    private byte[] imageOfCardAsAnArray;

    @Size(max = 1000)
    private String description;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public byte[] getImageOfCardAsAnArray() {
        return imageOfCardAsAnArray;
    }

    public void setImageOfCardAsAnArray(byte[] imageOfCardAsAnArray) {
        this.imageOfCardAsAnArray = imageOfCardAsAnArray;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

