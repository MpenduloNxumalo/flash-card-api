package com.example.rest.webservices.flash_card_api.services;

import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;
import com.example.rest.webservices.flash_card_api.repositories.DeckRepository;
import com.example.rest.webservices.flash_card_api.services.service_interfaces.DeckServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeckService implements DeckServiceInterface {
    private DeckRepository repository;

    public DeckService(DeckRepository repository) {
        this.repository = repository;
    }

    public OperationStatus createDeck(Deck deck){
        String id = repository.createDeck(deck);
        return new OperationStatus().isSuccessful(true).deckId(id);
    }

    public List<Deck> retrieveAllDecks(){
        return repository.retrieveAllDecks();
    }

    @Override
    public Deck retrieveDeckById(String id) {
        return repository.retrieveDeckById(id);
    }

    @Override
    public List<Deck> retrieveDeckByName(String name) {
        return repository.retrieveDeckByName(name);
    }

    @Override
    public String addCardIdToDeckById(String id, String cardId) {
        return repository.addCardIdToDeckById(id, cardId);
    }
    @Override
    public String deleteCardIdFromDeckById(String id, String cardId) {
        return repository.deleteCardIdFromDeckById(id, cardId);
    }

    @Override
    public String updateDeckNameById(String id, String name) {
        return repository.updateDeckNameById(id, name);
    }

    @Override
    public String deleteDeckById(String id) {
        return repository.deleteDeckById(id);
    }

    @Override
    public List<Map<String,String>> deleteDeckByName(String name) {
        return repository.deleteDeckByName(name);
    }
}
