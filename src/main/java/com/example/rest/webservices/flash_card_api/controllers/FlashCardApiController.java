package com.example.rest.webservices.flash_card_api.controllers;

import com.example.rest.webservices.flash_card_api.api_interfaces.DeckApi;
import com.example.rest.webservices.flash_card_api.models.Deck;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlashCardApiController implements DeckApi {

    //These are methods related to creating the Deck
    @Override
    public ResponseEntity<Deck> createDeck(Deck body) {
        return null;
    }

    //These are methods related to retrieving the Deck
    @Override
    public ResponseEntity<List<Deck>> retrieveAllDecks() {
        return null;
    }

    @Override
    public ResponseEntity<Deck> retrieveDeckById(Integer id) {
        return null;
    }

    @Override
    public ResponseEntity<List<Deck>> retrieveDeckByName(String name) {
        return null;
    }

    //These are methods related to updating the Deck
    @Override
    public ResponseEntity<String> addCardIdToDeckById(Integer id, String cardId) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteCardIdFromDeckById(Integer id, String cardId) {
        return null;
    }

    @Override
    public ResponseEntity<String> updateDeckNameById(Integer id, String name) {
        return null;
    }

    //These are methods related to deleting the Deck
    @Override
    public ResponseEntity<String> deleteDeckById(Integer id) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteDeckByName(String name) {
        return null;
    }
}
