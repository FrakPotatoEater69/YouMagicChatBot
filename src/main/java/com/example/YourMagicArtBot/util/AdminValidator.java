package com.example.YourMagicArtBot.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminValidator {
    @Value("${owners}")
    private String stringOwners;

    private List<Long> owners = new ArrayList<>();

    @PostConstruct
    private void convertIdFromStringToLong() {
        if (stringOwners.length() > 9) {
            String[] stringIds = stringOwners.split(", ");
            for (String id : stringIds) {
                owners.add(Long.parseLong(id));
            }
        } else {
            owners.add(Long.parseLong(stringOwners));
        }
    }

    public boolean isOwner(Long id) {
        return owners.contains(id);
    }

}
