package com.example.rest.webservices.flash_card_api.repositories;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.repositories.repository_interfaces.DeckRepositoryInterface;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

@Repository
@AllArgsConstructor
public class DeckRepository implements DeckRepositoryInterface {

  private Firestore firestore;

  @Override
  public String createDeck(Deck deck) {
    if (deck == null) {
      throw new RuntimeException("Deck argument is null");
    }
    try {
      ApiFuture<DocumentReference> decks =
          firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).add(deck);
      return decks.get().getId();
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching flashcard", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while creating decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public List<Deck> retrieveAllDecks() {
    try {
      List<Deck> deckList = new ArrayList<>();
      ApiFuture<QuerySnapshot> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).get();
      for (DocumentSnapshot doc : future.get().getDocuments()) {
        Deck deck = doc.toObject(Deck.class);
        if (deck != null) {
          deck.deckId(doc.getId());
          deckList.add(deck);
        }
      }
      return deckList;
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        // Handle specific Firestore errors
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching Deck", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // restore interrupt flag
      throw new RuntimeException("Thread interrupted while fetching decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public Deck retrieveDeckById(String id) throws NotFoundException {
    try {
      if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()) {
        DocumentSnapshot snapshot =
            firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get();
        Deck deck = snapshot.toObject(Deck.class);
        if (deck != null) {
          deck.deckId(snapshot.getId());
          return deck;
        } else {
          throw new RuntimeException("Error occurred retrieving DocumentSnapshot");
        }
      } else {
        throw new NotFoundException(String.format("Deck Id: %s does not exist", id));
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching Deck", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while fetching deck", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public List<Deck> retrieveDeckByName(String name) throws NotFoundException {
    try {
      List<Deck> deckList = new ArrayList<>();
      ApiFuture<QuerySnapshot> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION)
          .whereEqualTo(DECK_NAME_FIELD, name).get();

      QuerySnapshot snapshot = future.get();
      for (DocumentSnapshot doc : snapshot.getDocuments()) {
        Deck deck = doc.toObject(Deck.class);
        if (deck != null) {
          deck.deckId(doc.getId());
          deckList.add(deck);
        } else {
          throw new RuntimeException("Document data cannot be mapped to your Deck class");
        }
      }

      if (deckList.isEmpty()) {
        throw new NotFoundException(
            String.format("Deck with the following name: %s does not exist", name));
      } else {
        return deckList;
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching decks", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while fetching decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public String addCardIdToDeckById(String id, String cardId) throws NotFoundException {
    try {
      if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()) {
        ApiFuture<WriteResult> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION)
            .document(id).update(CARD_ID_FIELD, FieldValue.arrayUnion(cardId));
        WriteResult writeResult = future.get();
        return writeResult.getUpdateTime().toString();
      } else {
        throw new NotFoundException(String.format("Deck Id: %s does not exist", id));
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error updating deck", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while updating decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public String deleteCardIdFromDeckById(String id, String cardId) throws NotFoundException {
    try {
      if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()) {
        ApiFuture<WriteResult> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION)
            .document(id).update(CARD_ID_FIELD, FieldValue.arrayRemove(cardId));
        return future.get().getUpdateTime().toString();
      } else {
        throw new NotFoundException(String.format("Deck Id: %s does not exist", id));
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching flashcard", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while updating decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public String updateDeckNameById(String id, String name) throws NotFoundException {
    try {
      if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(DECK_NAME_FIELD, name);
        ApiFuture<WriteResult> future =
            firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).update(updates);
        return future.get().getUpdateTime().toString();
      } else {
        throw new NotFoundException(String.format("Deck Id: %s does not exist", id));
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching flashcard", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while updating decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public String deleteDeckById(String id) throws NotFoundException {
    try {
      if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()) {
        ApiFuture<WriteResult> future =
            firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).delete();
        WriteResult writeResult = future.get();
        return writeResult.getUpdateTime().toString();
      } else {
        throw new NotFoundException(String.format("Deck Id: %s does not exist", id));
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching flashcard", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while deleting decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }

  @Override
  public List<Map<String, String>> deleteDeckByName(String name) throws NotFoundException {
    try {
      List<Map<String, String>> deleteList = new ArrayList<>();
      ApiFuture<QuerySnapshot> querySnapshot = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION)
          .whereEqualTo(DECK_NAME_FIELD, name).get();
      List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
      if (documents.isEmpty()) {
        throw new NotFoundException(String.format("Deck with the name: %s does not exist", name));
      }
      for (QueryDocumentSnapshot doc : documents) {
        ApiFuture<WriteResult> deleteFuture = doc.getReference().delete();
        Map<String, String> map =
            Map.of(doc.getId(), deleteFuture.get().getUpdateTime().toString());
        deleteList.add(map);
      }
      return deleteList;
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof FirestoreException firestoreEx) {
        throw new RuntimeException("Firestore error: " + firestoreEx.getCode());
      }
      throw new RuntimeException("Error fetching flashcard", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while deleting decks", e);
    } catch (CancellationException e) {
      throw new RuntimeException("Computation was cancelled");
    }
  }
}
