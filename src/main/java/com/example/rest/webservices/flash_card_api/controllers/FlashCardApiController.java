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

  // These are methods related to creating the Deck
  @Override
  public ResponseEntity<OperationStatus> createDeck(Deck body) {
    if (body == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new OperationStatus().isSuccessful(false).error("Deck cannot be null"));
    }

    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(body.getDeckId());
    OperationStatus nameValidationOperationStatus = ControllerUtil.validateName(body.getName());

    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new OperationStatus().error(deckIdValidationOperationStatus.getError()));
    }

    if (!nameValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new OperationStatus().error(nameValidationOperationStatus.getError()));
    }

    try {
      return ResponseEntity.status(HttpStatus.OK).body(deckService.createDeck(body));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().error(e.getMessage()));
    }

  }

  // These are methods related to retrieving the Deck
  @Override
  public ResponseEntity<DeckListResponseObject> retrieveAllDecks() {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new DeckListResponseObject().body(deckService.retrieveAllDecks()));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new DeckListResponseObject().error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<DeckResponseObject> retrieveDeckById(String deckId) {
    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.badRequest()
          .body(new DeckResponseObject().error(deckIdValidationOperationStatus.getError()));
    }

    try {
      Deck deck = deckService.retrieveDeckById(deckId);
      if (deck == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok().body(new DeckResponseObject().body(deck));
    } catch (RuntimeException e) {
      return ResponseEntity.internalServerError()
          .body(new DeckResponseObject().error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new DeckResponseObject().error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<DeckListResponseObject> retrieveDeckByName(String name) {
    OperationStatus nameValidationOperationStatus = ControllerUtil.validateId(name);

    if (!nameValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.badRequest()
          .body(new DeckListResponseObject().error(nameValidationOperationStatus.getError()));
    }

    try {
      return ResponseEntity.ok()
          .body(new DeckListResponseObject().body(deckService.retrieveDeckByName(name)));
    } catch (RuntimeException exception) {
      return ResponseEntity.internalServerError()
          .body(new DeckListResponseObject().error(exception.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new DeckListResponseObject().error(e.getMessage()));
    }
  }

  // These are methods related to updating the Deck
  @Override
  public ResponseEntity<OperationStatus> addCardIdToDeckById(String deckId, String flashCardId) {
    OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateId(flashCardId);
    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

    if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(flashCardIdValidationOperationStatus);
    }
    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deckIdValidationOperationStatus);
    }

    try {
      String timestamp = deckService.addCardIdToDeckById(deckId, flashCardId);
      if (timestamp == null) {
        return ResponseEntity.internalServerError().body(new OperationStatus().isSuccessful(false)
            .error("addCardIdToDeckById method returned null"));
      } else if (timestamp.isEmpty()) {
        return ResponseEntity.internalServerError().body(new OperationStatus().isSuccessful(false)
            .error("addCardIdToDeckById method returned empty string"));
      } else {
        return ResponseEntity.ok()
            .body(new OperationStatus().isSuccessful(true).timestamp(timestamp));
      }
    } catch (RuntimeException e) {
      return ResponseEntity.internalServerError().body(new OperationStatus().error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<OperationStatus> deleteCardIdFromDeckById(String deckId,
      String flashCardId) {
    OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateId(flashCardId);
    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

    if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(flashCardIdValidationOperationStatus);
    }
    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deckIdValidationOperationStatus);
    }

    try {
      String timestamp = deckService.deleteCardIdFromDeckById(deckId, flashCardId);
      return ResponseEntity.status(HttpStatus.OK)
          .body(new OperationStatus().isSuccessful(true).timestamp(timestamp));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<OperationStatus> updateDeckNameById(String deckId, String name) {
    OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateName(name);
    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

    if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(flashCardIdValidationOperationStatus);
    }
    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deckIdValidationOperationStatus);
    }

    try {
      String timestamp = deckService.updateDeckNameById(deckId, name);
      return ResponseEntity.status(HttpStatus.OK)
          .body(new OperationStatus().isSuccessful(true).timestamp(timestamp));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().error(e.getMessage()));
    }
  }

  // These are methods related to deleting the Deck
  @Override
  public ResponseEntity<OperationStatus> deleteDeckById(String deckId) {
    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deckIdValidationOperationStatus);
    }

    try {
      String timestamp = deckService.deleteDeckById(deckId);
      return ResponseEntity.status(HttpStatus.OK)
          .body(new OperationStatus().isSuccessful(true).timestamp(timestamp));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
    }

  }

  @Override
  public ResponseEntity<OperationStatusListResponseObject> deleteDeckByName(String name) {
    OperationStatus nameValidationOperationStatus = ControllerUtil.validateName(name);

    if (!nameValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          new OperationStatusListResponseObject().error(nameValidationOperationStatus.getError()));
    }

    try {
      List<OperationStatus> responses = new ArrayList<>();
      List<Map<String, String>> deckServiceResponses = deckService.deleteDeckByName(name);

      for (Map<String, String> response : deckServiceResponses) {
        for (Map.Entry<String, String> entry : response.entrySet()) {
          if (!entry.getKey().isEmpty() || !entry.getValue().isEmpty()) {
            responses.add(new OperationStatus().deckId(entry.getKey()).timestamp(entry.getValue()));
          }
        }
      }
      if (responses.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new OperationStatusListResponseObject()
                .error(String.format("Deck with the name: %s does not exist", name)));
      }
      return ResponseEntity.status(HttpStatus.OK)
          .body(new OperationStatusListResponseObject().body(responses));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatusListResponseObject().error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatusListResponseObject().error(e.getMessage()));
    }
  }

  // These are methods related to creating the FlashCard
  @Override
  public ResponseEntity<OperationStatus> createFlashCard(String deckId, FlashCard body) {
    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new OperationStatus().error(deckIdValidationOperationStatus.getError()));
    }

    if (body == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new OperationStatus().error("Flashcard cannot be null"));
    }
    if (body.getFront() == null || body.getFront().trim().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new OperationStatus().error("Flash card title cannot be null or empty"));
    }
    if (body.getBack() == null || body.getBack().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new OperationStatus().error("Flash card content cannot be null or empty"));
    }

    try {
      String flashCardId = flashCardService.createFlashCard(deckId, body);

      return ResponseEntity.status(HttpStatus.OK)
          .body(new OperationStatus().isSuccessful(true)
              .timestamp(deckService.addCardIdToDeckById(deckId, flashCardId))
              .flashCardId(flashCardId));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().error(e.getMessage()));
    }
  }

  // These are methods related to retrieving the FlashCard
  @Override
  public ResponseEntity<FlashCardListResponseObject> retrieveAllFlashCards() {
    try {
      List<FlashCard> flashCardList = flashCardService.retrieveAllFlashCards();
      return ResponseEntity.status(HttpStatus.OK)
          .body(new FlashCardListResponseObject().body(flashCardList));
    } catch (RuntimeException exception) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new FlashCardListResponseObject().error(exception.getMessage()));
    }
  }

  @Override
  public ResponseEntity<FlashCardResponseObject> retrieveFlashCardById(String flashCardId) {
    OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateId(flashCardId);

    if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new FlashCardResponseObject().error(flashCardIdValidationOperationStatus.getError()));
    }

    try {
      FlashCard flashCard = flashCardService.retrieveFlashCardById(flashCardId);
      return ResponseEntity.status(HttpStatus.OK).body(new FlashCardResponseObject()
          .body(Objects.requireNonNullElseGet(flashCard, FlashCard::new)));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new FlashCardResponseObject().error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<FlashCardListResponseObject> retrieveFlashCardByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new FlashCardListResponseObject()
          .error("Flash Card Name cannot be null or an empty string"));
    }

    try {
      return ResponseEntity.status(HttpStatus.OK).body(
          new FlashCardListResponseObject().body(flashCardService.retrieveFlashCardByName(name)));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new FlashCardListResponseObject().error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<FlashCardListResponseObject> retrieveAllFlashCardsInDeck(String deckId) {
    OperationStatus deckIdValidationOperationStatus = ControllerUtil.validateId(deckId);

    if (!deckIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new FlashCardListResponseObject().error(deckIdValidationOperationStatus.getError()));
    }

    try {
      return ResponseEntity.status(HttpStatus.OK).body(new FlashCardListResponseObject()
          .body(flashCardService.retrieveAllFlashCardsInDeck(deckId)));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new FlashCardListResponseObject().error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new FlashCardListResponseObject().error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<OperationStatus> updateFlashCardTitleById(String flashcardId,
      String title) {
    OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateId(flashcardId);

    if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.badRequest().body(flashCardIdValidationOperationStatus);
    }

    if (title == null || title.trim().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OperationStatus()
          .isSuccessful(false).error("FlashCard title cannot be null or an empty string"));
    }
    try {
      String updateTimeStamp = flashCardService.updateFlashCardTitleById(flashcardId, title);
      return ResponseEntity.status(HttpStatus.OK).body(new OperationStatus().isSuccessful(true)
          .timestamp(updateTimeStamp).flashCardId(flashcardId));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<OperationStatus> updateFlashCardContentById(String id, String content) {
    OperationStatus flashCardIdValidationOperationStatus = ControllerUtil.validateId(id);
    if (!flashCardIdValidationOperationStatus.isIsSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(flashCardIdValidationOperationStatus);
    }

    if (content == null || content.trim().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OperationStatus()
          .isSuccessful(false).error("FlashCard content cannot be null or an empty string"));
    }
    try {
      String updateTimeStamp = flashCardService.updateFlashCardContentById(id, content);
      return ResponseEntity.status(HttpStatus.OK).body(
          new OperationStatus().isSuccessful(true).timestamp(updateTimeStamp).flashCardId(id));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
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
        String flashCardIdRemovalFromDeckTimeStamp =
            deckService.deleteCardIdFromDeckById(deckId, flashcardId);
        if (flashCardIdRemovalFromDeckTimeStamp != null) {
          return ResponseEntity.status(HttpStatus.OK)
              .body(new OperationStatus().isSuccessful(true)
                  .timestamp(flashCardIdRemovalFromDeckTimeStamp).flashCardId(flashcardId)
                  .deckId(deckId));
        } else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new OperationStatus()
              .isSuccessful(false).error("flashCardIdRemovalFromDeckTimeStamp is null"));
        }
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new OperationStatus().isSuccessful(false).error("flashCardDeletionTimeStamp"));
      }
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new OperationStatus().isSuccessful(false).error(e.getMessage()));
    }
  }
}
