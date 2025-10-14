package com.example.rest.webservices.flash_card_api.services;

import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;
import com.example.rest.webservices.flash_card_api.repositories.DeckRepository;
import com.example.rest.webservices.flash_card_api.repositories.FlashCardRepository;
import com.example.rest.webservices.flash_card_api.services.service_interfaces.FlashCardServiceInterface;
import org.springframework.stereotype.Service;

@Service
public class FlashCardService implements FlashCardServiceInterface {
    private FlashCardRepository repository;
    private DeckService deckService;

    public FlashCardService(FlashCardRepository repository, DeckService deckService) {
        this.repository = repository;
        this.deckService = deckService;
    }


    @Override
    public String createFlashCard(String deckId, FlashCard body) {
        String id = repository.createFlashCard(body);
        if (!id.isEmpty()) {
            return deckService.addCardIdToDeckById(deckId, id) + ";" + id;
        } else {
            return "";
        }
    }

    @Override
    public String retrieveAllFlashCards() {
        return null;
    }
}
