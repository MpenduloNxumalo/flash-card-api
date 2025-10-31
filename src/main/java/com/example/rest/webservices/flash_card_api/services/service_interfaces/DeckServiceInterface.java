package com.example.rest.webservices.flash_card_api.services.service_interfaces;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;

import java.util.List;
import java.util.Map;

public interface DeckServiceInterface {

  OperationStatus createDeck(Deck deck);

  List<Deck> retrieveAllDecks();

  Deck retrieveDeckById(String id) throws NotFoundException;

  List<Deck> retrieveDeckByName(String name) throws NotFoundException;

  String addCardIdToDeckById(String id, String cardId) throws NotFoundException;

  String deleteCardIdFromDeckById(String id, String cardId) throws NotFoundException;

  String updateDeckNameById(String id, String name) throws NotFoundException;

  String deleteDeckById(String id) throws NotFoundException;

  List<Map<String, String>> deleteDeckByName(String name) throws NotFoundException;
}
