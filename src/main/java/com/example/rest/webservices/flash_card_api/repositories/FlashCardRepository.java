package com.example.rest.webservices.flash_card_api.repositories;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.example.rest.webservices.flash_card_api.repositories.repository_interfaces.FlashCardRepositoryInterface;
import com.example.rest.webservices.flash_card_api.utils.FlashCardRepositoryUtil;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

@Repository
@AllArgsConstructor
public class FlashCardRepository implements FlashCardRepositoryInterface {
    private Firestore firestore;
    private DeckRepository deckRepository;

    @Override
    public String createFlashCard(FlashCard flashCard) {
        if (flashCard == null) {
            throw new RuntimeException("Flash card argument is null");
        }
        try {
            ApiFuture<DocumentReference> flashCards = firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).add(flashCard);
            DocumentReference documentReference = flashCards.get();
            String flashCardId = documentReference.getId();
            if (!flashCardId.isEmpty() || flashCardId != null) {
                return flashCardId;
            } else {
                throw new RuntimeException("Error in creating new flash card. Flash Card Id returned empty or null");
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FirestoreException firestoreEx) {
                // Handle specific Firestore errors
                throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
            }
            throw new RuntimeException("Error fetching flashcards", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt flag
            throw new RuntimeException("Thread interrupted while fetching flashcards", e);
        }
    }

    @Override
    public List<FlashCard> retrieveAllFlashCards() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).get();
            return FlashCardRepositoryUtil.getFlashCardsList(future);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FirestoreException firestoreEx) {
                // Handle specific Firestore errors
                System.err.println("Firestore error: " + firestoreEx.getCode());
            }
            throw new RuntimeException("Error fetching flashcards", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt flag
            throw new RuntimeException("Thread interrupted while fetching flashcards", e);
        }
    }

    @Override
    public FlashCard retrieveFlashCardById(String id) {
        try {
            if (id == null) {
                throw new RuntimeException("id argument is null");
            }
            ApiFuture<DocumentSnapshot> doc = firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).document(id).get();
            DocumentSnapshot snapshot = doc.get();
            if (snapshot == null) {
                return new FlashCard();
            }
            FlashCard flashCard = snapshot.toObject(FlashCard.class);
            if (flashCard != null) {
                flashCard.id(snapshot.getId());
                return flashCard;
            } else {
                return new FlashCard();
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FirestoreException firestoreEx) {
                // Handle specific Firestore errors
                throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
            }
            throw new RuntimeException("Error fetching flashcard", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt flag
            throw new RuntimeException("Thread interrupted while fetching flashcards", e);
        } catch (CancellationException e) {
            throw new RuntimeException("Computation was cancelled");
        }
    }

    @Override
    public List<FlashCard> retrieveFlashCardByName(String name) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION)
                    .whereEqualTo(FLASHCARD_TITLE_FIELD, name).get();
            return FlashCardRepositoryUtil.getFlashCardsList(future);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FirestoreException firestoreEx) {
                throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
            }
            throw new RuntimeException("Error fetching flashcard", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while fetching flashcards", e);
        } catch (CancellationException e) {
            throw new RuntimeException("Computation was cancelled");
        }
    }

    @Override
    public List<FlashCard> retrieveAllFlashCardsInDeck(String deckId) throws NotFoundException {
        try {
            Deck deck = deckRepository.retrieveDeckById(deckId);
            List<FlashCard> flashCardList = new ArrayList<>();
            if (deck == null) {
                return flashCardList;
            }
            List<String> flashCardIds = deck.getCardIds();

            for (String flashCardId : flashCardIds) {
                if (flashCardId == null) {
                    continue;
                }
                FlashCard flashCard = retrieveFlashCardById(flashCardId);
                if (flashCard != null) {
                    flashCardList.add(flashCard);
                }
            }
            return flashCardList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String updateFlashCardTitleById(String id, String title) throws NotFoundException {
        return FlashCardRepositoryUtil.updateFlashCard(firestore, id, FLASHCARD_TITLE_FIELD, title);
    }

    @Override
    public String updateFlashCardContentById(String id, String content) throws NotFoundException {
        return FlashCardRepositoryUtil.updateFlashCard(firestore, id, FLASHCARD_CONTENT_FIELD, content);
    }

    @Override
    public String deleteFlashCardById(String flashcardId) throws NotFoundException {
        try {
            if (firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).document(flashcardId).get().get().exists()) {
                ApiFuture<WriteResult> future = firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).document(flashcardId).delete();
                return future.get().getUpdateTime().toString();
            } else {
                throw new NotFoundException(String.format("Id: %s does not exist", flashcardId));
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FirestoreException firestoreEx) {
                throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
            }
            throw new RuntimeException("Error fetching flashcard", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while fetching flashcards", e);
        } catch (CancellationException e) {
            throw new RuntimeException("Computation was cancelled");
        }
    }
}
