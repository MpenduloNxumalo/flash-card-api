package com.example.rest.webservices.flash_card_api.repositories.repository_interfaces;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.FlashCard;

import java.util.List;

public interface FlashCardRepositoryInterface {
    String PATH_NAME_FOR_FLASH_CARD_COLLECTION = "FlashCard";
    String FLASHCARD_TITLE_FIELD = "front";
    String FLASHCARD_CONTENT_FIELD = "back";

    String createFlashCard(FlashCard flashCard);
    List<FlashCard> retrieveAllFlashCards();
    FlashCard retrieveFlashCardById(String id);
    List<FlashCard> retrieveFlashCardByName(String name);
    List<FlashCard> retrieveAllFlashCardsInDeck(String deckId);
    String updateFlashCardTitleById(String id, String title) throws NotFoundException;

    String updateFlashCardContentById(String id, String content) throws NotFoundException;
    String deleteFlashCardById(String flashcardId) throws NotFoundException;
}
