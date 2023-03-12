package com.example.YourMagicArtBot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class UniversePrediction {


    @Size(max = 1000)
    @Column
    private String description;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
