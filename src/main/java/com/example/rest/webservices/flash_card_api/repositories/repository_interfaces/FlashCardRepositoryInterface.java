package com.example.rest.webservices.flash_card_api.repositories.repository_interfaces;

import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.models.FlashCard;

import java.util.List;

public interface FlashCardRepositoryInterface {
    String PATH_NAME_FOR_FLASH_CARD_COLLECTION = "FlashCard";

    String createFlashCard(FlashCard flashCard);
    List<Deck> retrieveAllFlashCards();
}
