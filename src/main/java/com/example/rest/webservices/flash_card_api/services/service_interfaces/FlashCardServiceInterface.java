package com.example.rest.webservices.flash_card_api.services.service_interfaces;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.FlashCard;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FlashCardServiceInterface {
    String createFlashCard(String deckId, FlashCard body);
    List<FlashCard> retrieveAllFlashCards();
    FlashCard retrieveFlashCardById(String id);
    List<FlashCard> retrieveFlashCardByName(String name);
    List<FlashCard> retrieveAllFlashCardsInDeck(String deckId) throws NotFoundException;
    String updateFlashCardTitleById(String id, String title) throws NotFoundException;
    String updateFlashCardContentById(String id, String content) throws NotFoundException;
    String deleteFlashCardById(String flashcardId) throws NotFoundException;
}
