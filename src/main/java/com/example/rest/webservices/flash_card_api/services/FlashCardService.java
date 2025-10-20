package com.example.rest.webservices.flash_card_api.services;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;
import com.example.rest.webservices.flash_card_api.repositories.FlashCardRepository;
import com.example.rest.webservices.flash_card_api.services.service_interfaces.FlashCardServiceInterface;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FlashCardService implements FlashCardServiceInterface {
    private FlashCardRepository repository;
    private final DeckService deckService;

    public FlashCardService(FlashCardRepository repository, DeckService deckService) {
        this.repository = repository;
        this.deckService = deckService;
    }


    @Override
    public String createFlashCard(String deckId, FlashCard body) {
        return repository.createFlashCard(body);
    }

    @Override
    public List<FlashCard> retrieveAllFlashCards() {
        return repository.retrieveAllFlashCards();
    }

    @Override
    public FlashCard retrieveFlashCardById(String id) {
        return repository.retrieveFlashCardById(id);
    }

    @Override
    public List<FlashCard> retrieveFlashCardByName(String name) {
        return repository.retrieveFlashCardByName(name);
    }

    @Override
    public List<FlashCard> retrieveAllFlashCardsInDeck(String deckId) throws NotFoundException {
        return repository.retrieveAllFlashCardsInDeck(deckId);
    }

    @Override
    public String updateFlashCardTitleById(String id, String title) throws NotFoundException {
        return repository.updateFlashCardTitleById(id, title);
    }

    @Override
    public String updateFlashCardContentById(String id, String content) throws NotFoundException {
        return repository.updateFlashCardContentById(id, content);
    }

    @Override
    public String deleteFlashCardById(String flashcardId) throws NotFoundException {
        return repository.deleteFlashCardById(flashcardId);
    }
}
