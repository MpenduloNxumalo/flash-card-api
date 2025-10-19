package com.example.rest.webservices.flash_card_api.controllers;

import com.example.rest.webservices.flash_card_api.api_interfaces.DeckApi;
import com.example.rest.webservices.flash_card_api.api_interfaces.FlashCardApi;
import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.*;
import com.example.rest.webservices.flash_card_api.services.DeckService;
import com.example.rest.webservices.flash_card_api.services.FlashCardService;
import com.example.rest.webservices.flash_card_api.utils.ControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
    public ResponseEntity<ResponseObject> retrieveAllDecks() {
        try{
            return ResponseEntity.ok().body(
                    new ResponseObject().body(deckService.retrieveAllDecks())
            );
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(
                    new ResponseObject().error(e.getMessage())
            );
        }
    }

    @Override
    public ResponseEntity<ResponseObject> retrieveDeckById(String id) {
        try {
            Deck deck = deckService.retrieveDeckById(id);
            if (deck == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new ResponseObject().body(deck));
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(
                    new ResponseObject().error(
                            e.getMessage()
                    )
            );
        }

    }

    @Override
    public ResponseEntity<ResponseObject> retrieveDeckByName(String name) {
        return ResponseEntity.ok(new ResponseObject().body(deckService.retrieveDeckByName(name)));
    }

    //These are methods related to updating the Deck
    @Override
    public ResponseEntity<OperationStatus> addCardIdToDeckById(String id, String cardId) {
        if (ControllerUtil.isParametersEmpty(id, cardId)){
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
        if (ControllerUtil.isParametersEmpty(id, cardId)){
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
        if (ControllerUtil.isParametersEmpty(id, name)){
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

        if (ControllerUtil.isParametersEmpty(id, null)){
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

        if (ControllerUtil.isParametersEmpty(null, name)){
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
    public ResponseEntity<OperationStatus> createFlashCard(String deckId, FlashCard body)   {

        if (deckId == null || deckId.trim().isEmpty()) {
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
        if (body.getFront() == null || body.getFront().trim().isEmpty()) {
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
        String flashCardId = flashCardService.createFlashCard(deckId, body);

        if (!flashCardId.trim().isEmpty()) {
            return ResponseEntity.ok().body(
                    new OperationStatus()
                            .isSuccessful(true)
                            .timestamp(deckService.addCardIdToDeckById(deckId, flashCardId))
                            .flashCardId(flashCardId));
        } else {
            return ResponseEntity.ok().body(
                    new OperationStatus()
                            .isSuccessful(false)
                            .error("The resulting flash card id response is empty")
                    );
        }

    }

    //These are methods related to retrieving the FlashCard
    @Override
    public ResponseEntity<FlashCardListResponseObject> retrieveAllFlashCards() {
        try {
            List<FlashCard> flashCardList = flashCardService.retrieveAllFlashCards();
            return ResponseEntity.ok(new FlashCardListResponseObject().body(flashCardList));
        } catch (RuntimeException exception){
            return ResponseEntity.internalServerError().body(
                    new FlashCardListResponseObject().error(
                            exception.getMessage()
                    )
            );
        }
    }

    @Override
    public ResponseEntity<FlashCardResponseObject> retrieveFlashCardById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new FlashCardResponseObject().error("Flash Card Id cannot be null")
            );
        }

        if (!id.matches(ControllerUtil.ALPHANUMERIC_REGEX)) {
            return ResponseEntity.badRequest().body(
                    new FlashCardResponseObject().error("Flash Card Id must contain only alphanumeric characters")
            );
        }
        try {
            FlashCard flashCard = flashCardService.retrieveFlashCardById(id);
            return ResponseEntity.ok().body(
                    new FlashCardResponseObject().body(
                            Objects.requireNonNullElseGet(flashCard, FlashCard::new)
                    )
            );
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(
                    new FlashCardResponseObject().error(e.getMessage())
            );
        }
    }

    @Override
    public ResponseEntity<FlashCardListResponseObject> retrieveFlashCardByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new FlashCardListResponseObject().error("Flash Card Name cannot be null or an empty string")
            );
        }

        try {
            return ResponseEntity.ok().body(
                    new FlashCardListResponseObject().body(
                            flashCardService.retrieveFlashCardByName(name)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(
                    new FlashCardListResponseObject().error(
                            e.getMessage()
                    )
            );
        }
    }

    @Override
    public ResponseEntity<FlashCardListResponseObject> retrieveAllFlashCardsInDeck(String deckId) {
        if (deckId == null || deckId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new FlashCardListResponseObject().error("Deck Id cannot be null or an empty string")
            );
        }
        if (!deckId.matches(ControllerUtil.ALPHANUMERIC_REGEX)) {
            return ResponseEntity.badRequest().body(
                    new FlashCardListResponseObject().error("Deck Id must contain only alphanumeric characters")
            );
        }
        try {
            return ResponseEntity.ok().body(
                    new FlashCardListResponseObject().body(
                            flashCardService.retrieveAllFlashCardsInDeck(deckId)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(
                    new FlashCardListResponseObject().error(
                            e.getMessage()
                    )
            );
        }
    }

    @Override
    public ResponseEntity<OperationStatus> updateFlashCardTitleById(String id, String title) {
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus()
                            .isSuccessful(false)
                            .error("FlashCard Id cannot be null or an empty string")
            );
        } else if (!id.matches(ControllerUtil.ALPHANUMERIC_REGEX)) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus()
                            .isSuccessful(false)
                            .error("FlashCard Id must contain only alphanumeric characters")
            );
        } else if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus()
                            .isSuccessful(false)
                            .error("FlashCard title cannot be null or an empty string")
            );
        }
        try {
            String updateTimeStamp = flashCardService.updateFlashCardTitleById(id, title);
            return ResponseEntity.ok().body(
                    new OperationStatus()
                            .isSuccessful(true)
                            .timestamp(updateTimeStamp)
                            .flashCardId(id)
            );

        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(
                    new OperationStatus()
                            .isSuccessful(false)
                            .error(e.getMessage())
            );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new OperationStatus()
                            .isSuccessful(false)
                            .error(e.getMessage())
            );
        }
    }

    @Override
    public ResponseEntity<OperationStatus> updateFlashCardContentById(String id, String content) {
        OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateId(id);
        if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
            return ResponseEntity.badRequest().body(flashCardIdValidationOperationStatus);
        }

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new OperationStatus().isSuccessful(false)
                            .error("FlashCard content cannot be null or an empty string")
            );
        }
        try {
            String updateTimeStamp = flashCardService.updateFlashCardContentById(id, content);
            return ResponseEntity.ok().body(
                    new OperationStatus().isSuccessful(true).timestamp(updateTimeStamp).flashCardId(id)
            );
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(
                    new OperationStatus().isSuccessful(false).error(e.getMessage())
            );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new OperationStatus().isSuccessful(false).error(e.getMessage())
            );
        }
    }

    @Override
    public ResponseEntity<OperationStatus> deleteFlashCardById(String flashcardId, String deckId) {
        OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateId(flashcardId);
        OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

        if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
            return ResponseEntity.badRequest().body(flashCardIdValidationOperationStatus);
        }
        if (!deckIdValidationOperationStatus.isIsSuccessful()) {
            return ResponseEntity.badRequest().body(deckIdValidationOperationStatus);
        }
        try {
            String flashCardDeletionTimeStamp = flashCardService.deleteFlashCardById(flashcardId);
            if (flashCardDeletionTimeStamp != null) {
                String flashCardIdRemovalFromDeckTimeStamp = deckService.deleteCardIdFromDeckById(deckId,flashcardId);
                if (flashCardIdRemovalFromDeckTimeStamp != null) {
                    return ResponseEntity.ok().body(
                            new OperationStatus().isSuccessful(true).timestamp(flashCardIdRemovalFromDeckTimeStamp)
                                    .flashCardId(flashcardId).deckId(deckId)
                    );
                } else {
                    return ResponseEntity.internalServerError().body(
                            new OperationStatus().isSuccessful(false)
                                    .error("flashCardIdRemovalFromDeckTimeStamp is null")
                    );
                }
            } else {
                return ResponseEntity.internalServerError().body(
                        new OperationStatus().isSuccessful(false).error("flashCardDeletionTimeStamp")
                );
            }
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(
                    new OperationStatus().isSuccessful(false).error(e.getMessage())
            );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new OperationStatus().isSuccessful(false).error(e.getMessage())
            );
        }
    }
}
