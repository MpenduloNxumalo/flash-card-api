package com.example.rest.webservices.flash_card_api.services;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.example.rest.webservices.flash_card_api.repositories.FlashCardRepository;
import com.example.rest.webservices.flash_card_api.services.service_interfaces.FlashCardServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashCardService implements FlashCardServiceInterface {
    private final FlashCardRepository repository;

    public FlashCardService(FlashCardRepository repository) {
        this.repository = repository;
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
