package com.example.rest.webservices.flash_card_api.utils;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static com.example.rest.webservices.flash_card_api.repositories.repository_interfaces.FlashCardRepositoryInterface.PATH_NAME_FOR_FLASH_CARD_COLLECTION;

public final class FlashCardRepositoryUtil {
  public static List<FlashCard> getFlashCardsList(ApiFuture<QuerySnapshot> future)
      throws InterruptedException, ExecutionException {
    List<FlashCard> flashCardList = new ArrayList<>();
    QuerySnapshot snapshot = future.get();
    if (snapshot == null || snapshot.isEmpty()) {
      return flashCardList;
    }
    for (DocumentSnapshot doc : snapshot.getDocuments()) {
      if (doc == null || !doc.exists()) {
        continue;
      }
      FlashCard flashCard = doc.toObject(FlashCard.class);
      if (flashCard != null) {
        flashCard.id(doc.getId());
        flashCardList.add(flashCard);
      }
    }
    return flashCardList;
  }

  public static String updateFlashCard(Firestore firestore, String id, String fieldToBeUpdated,
      String update) throws NotFoundException {
    try {
      if (firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).document(id).get().get()
          .exists()) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(fieldToBeUpdated, update);
        ApiFuture<WriteResult> future =
            firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).document(id).update(updates);
        return future.get().getUpdateTime().toString();
      } else {
        throw new NotFoundException(String.format("Id: %s does not exist", id));
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
