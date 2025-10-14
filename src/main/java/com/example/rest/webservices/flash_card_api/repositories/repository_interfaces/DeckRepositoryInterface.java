package com.example.rest.webservices.flash_card_api.repositories.repository_interfaces;

import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;

import java.util.List;
import java.util.Map;

public interface DeckRepositoryInterface {
    String PATH_NAME_FOR_DECK_COLLECTION = "Deck";
    String NAME_FIELD = "name";
    String CARD_ID_FIELD = "cardIds";
    String createDeck(Deck deck);
    List<Deck> retrieveAllDecks();
    Deck retrieveDeckById(String id);
    List<Deck> retrieveDeckByName(String name);
    String addCardIdToDeckById(String id, String cardId);
    String deleteCardIdFromDeckById(String id, String cardId);
    String updateDeckNameById(String id, String name);
    String deleteDeckById(String id);
    List<Map<String,String>> deleteDeckByName(String name);
}
