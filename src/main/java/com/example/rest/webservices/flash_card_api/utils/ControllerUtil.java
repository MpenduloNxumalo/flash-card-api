package com.example.rest.webservices.flash_card_api.utils;

import com.example.rest.webservices.flash_card_api.models.OperationStatus;

import java.util.Objects;

public final class ControllerUtil {

    public static String ALPHANUMERIC_REGEX = "^[A-Za-z0-9]+$";
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

    public static OperationStatus validateId(String id) {
        if (id == null) {
            return new OperationStatus().isSuccessful(false).error("Id cannot be null");
        } else if (id.trim().isEmpty()) {
            return new OperationStatus().isSuccessful(false).error("Id cannot be an empty string");
        } else if (!id.matches(ALPHANUMERIC_REGEX)) {
            return new OperationStatus().isSuccessful(false).error("Id must contain only alphanumeric characters");
        } else {
            return new OperationStatus().isSuccessful(true);
        }
    }

    public static OperationStatus validateName(String name) {
        if (name == null ) {
            return new OperationStatus().isSuccessful(false).error("Name cannot be null");
        } else if (name.trim().isEmpty()) {
            return new OperationStatus().isSuccessful(false).error("Name cannot be an empty string");
        } else {
            return new OperationStatus().isSuccessful(true);
        }
    }
}
