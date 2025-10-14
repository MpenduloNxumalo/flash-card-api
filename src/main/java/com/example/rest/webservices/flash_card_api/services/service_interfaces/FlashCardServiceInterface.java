package com.example.rest.webservices.flash_card_api.services.service_interfaces;

import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface FlashCardServiceInterface {
    String createFlashCard(String deckId, FlashCard body);
    String retrieveAllFlashCards();
    //retrieveFlashCardById
    //retrieveFlashCardByName
}
