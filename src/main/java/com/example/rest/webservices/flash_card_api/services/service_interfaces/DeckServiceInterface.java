package com.example.rest.webservices.flash_card_api.services.service_interfaces;

import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;

import java.util.List;
import java.util.Map;

public interface DeckServiceInterface {

    OperationStatus createDeck(Deck deck);
    List<Deck> retrieveAllDecks();

    Deck retrieveDeckById(String id);

    List<Deck> retrieveDeckByName(String name);
    String addCardIdToDeckById(String id, String cardId);
    String deleteCardIdFromDeckById(String id, String cardId);
    String updateDeckNameById(String id, String name);
    String deleteDeckById(String id);
    List<Map<String,String>> deleteDeckByName(String name);
}
