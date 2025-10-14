package com.example.rest.webservices.flash_card_api.utils;

public class ApiUtil {
    public static boolean isParametersEmpty(String id, String cardId) {
        if (cardId != null && id != null){
            return cardId.isEmpty() || id.isEmpty();
        } else if (cardId == null && id != null) {
            return id.isEmpty();
        } else if(cardId != null && id == null){
            return cardId.isEmpty();
        } else {
            return false;
        }
    }
}
