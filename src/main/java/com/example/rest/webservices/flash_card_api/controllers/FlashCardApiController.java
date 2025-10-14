package com.example.rest.webservices.flash_card_api.controllers;

import com.example.rest.webservices.flash_card_api.api_interfaces.DeckApi;
import com.example.rest.webservices.flash_card_api.api_interfaces.FlashCardApi;
import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.example.rest.webservices.flash_card_api.models.OperationStatus;
import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.services.DeckService;
import com.example.rest.webservices.flash_card_api.services.FlashCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.rest.webservices.flash_card_api.utils.ApiUtil.isParametersEmpty;

@RestController
public class FlashCardApiController implements DeckApi, FlashCardApi {

    @Autowired
    private DeckService deckService;

    @Autowired
    private FlashCardService flashCardService;

    //These are methods related to creating the Deck
    @Override
    public ResponseEntity<OperationStatus> createDeck(Deck body) {
        if (body == null) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().isSuccessful(false).error("deck cannot be null")
            );
        } else if (body.getName() == null) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().isSuccessful(false).error("deck name value cannot be null")
            );
        } else if (Objects.equals(body.getName(), "")) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().isSuccessful(false).error("deck name value cannot be empty string")
            );
        } else if (body.getCardIds() == null) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().isSuccessful(false).error("deck cardIds cannot be null")
            );
        }
        return ResponseEntity.ok(deckService.createDeck(body));
    }

    //These are methods related to retrieving the Deck
    @Override
    public ResponseEntity<List<Deck>> retrieveAllDecks() {
        return ResponseEntity.ok(deckService.retrieveAllDecks());
    }

    @Override
    public ResponseEntity<Deck> retrieveDeckById(String id) {
        Deck deck = deckService.retrieveDeckById(id);
        if (deck == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deck);
    }

    @Override
    public ResponseEntity<List<Deck>> retrieveDeckByName(String name) {
        return ResponseEntity.ok(deckService.retrieveDeckByName(name));
    }

    //These are methods related to updating the Deck
    @Override
    public ResponseEntity<OperationStatus> addCardIdToDeckById(String id, String cardId) {
        if (isParametersEmpty(id, cardId)){
            return ResponseEntity.badRequest().build();
        }
        String timestamp = deckService.addCardIdToDeckById(id, cardId);
        if (timestamp.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OperationStatus().isSuccessful(true).timestamp(timestamp));
    }


    @Override
    public ResponseEntity<OperationStatus> deleteCardIdFromDeckById(String id, String cardId) {
        if (isParametersEmpty(id, cardId)){
            return ResponseEntity.badRequest().build();
        }
        String timestamp = deckService.deleteCardIdFromDeckById(id, cardId);
        if (timestamp.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OperationStatus().isSuccessful(true).timestamp(timestamp));
    }

    @Override
    public ResponseEntity<OperationStatus> updateDeckNameById(String id, String name) {
        if (isParametersEmpty(id, name)){
            return ResponseEntity.badRequest().build();
        }
        String timestamp = deckService.updateDeckNameById(id, name);
        if (timestamp.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OperationStatus().isSuccessful(true).timestamp(timestamp));
    }

    //These are methods related to deleting the Deck
    @Override
    public ResponseEntity<OperationStatus> deleteDeckById(String id) {

        if (isParametersEmpty(id, null)){
            return ResponseEntity.badRequest().build();
        }
        String timestamp = deckService.deleteDeckById(id);
        if (timestamp.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OperationStatus().isSuccessful(true).timestamp(timestamp));
    }

    @Override
    public ResponseEntity<List<OperationStatus>> deleteDeckByName(String name) {

        if (isParametersEmpty(null, name)){
            return ResponseEntity.badRequest().build();
        }
        List<OperationStatus> responses = new ArrayList<>();
        List<Map<String,String>> deckServiceResponses = deckService.deleteDeckByName(name);

        for(Map<String,String> response:deckServiceResponses){
            for (Map.Entry<String, String> entry:response.entrySet()){
                responses.add(new OperationStatus().deckId(entry.getKey()).timestamp(entry.getValue()));
            }
        }
        if (responses.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responses);
    }

    //These are methods related to creating the FlashCard
    @Override
    public ResponseEntity<OperationStatus> createFlashCard(String deckId, FlashCard body) {

        if (deckId == null || deckId.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().error(
                            "deckId cannot be null or empty"
                    )
            );
        }
        if (body == null) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().error(
                            "Flashcard cannot be null"
                    )
            );
        }
        if (body.getFront() == null || body.getFront().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().error(
                            "Flash card title cannot be null or empty"
                    )
            );
        }
        if (body.getBack() == null || body.getBack().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().error(
                            "Flash card content cannot be null or empty"
                    )
            );
        }
        String response = flashCardService.createFlashCard(deckId, body);
        return ResponseEntity.ok().body(
                new OperationStatus()
                        .isSuccessful(true)
                        .deckId(deckId)
                        .flashCardId(response.split(";")[1])
                        .timestamp(response.split(";")[0]
                        )
        );
    }

    //These are methods related to retrieving the FlashCard
    @Override
    public ResponseEntity<List<FlashCard>> retrieveAllFlashCards() {
        return null;
    }

    @Override
    public ResponseEntity<FlashCard> retrieveFlashCardById(String id) {
        return null;
    }

    @Override
    public ResponseEntity<List<FlashCard>> retrieveFlashCardByName(String name) {
        return null;
    }
}
