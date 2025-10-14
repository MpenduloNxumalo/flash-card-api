package com.example.rest.webservices.flash_card_api.repositories;

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
import java.util.concurrent.ExecutionException;

@Repository
@AllArgsConstructor
public class DeckRepository implements DeckRepositoryInterface {

    private Firestore firestore;

    @Override
    public String createDeck(Deck deck) {
        try {
            ApiFuture<DocumentReference> decks = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).add(deck);
            return decks.get().getId();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Deck> retrieveAllDecks() {
        try {
            List<Deck> deckList = new ArrayList<>();
            ApiFuture<QuerySnapshot> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).get();
            for (DocumentSnapshot doc : future.get().getDocuments()) {
                Deck deck = doc.toObject(Deck.class);
                if (deck != null) {
                    deck.deckId(doc.getId());
                }
                deckList.add(deck);
            }
            return deckList;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Deck retrieveDeckById(String id) {
        try {
            ApiFuture<DocumentSnapshot> doc = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get();
            Deck deck = doc.get().toObject(Deck.class);
            if (deck != null) {
                deck.deckId(doc.get().getId());
            }
            return deck;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Deck> retrieveDeckByName(String name) {
        try {
            List<Deck> deckList = new ArrayList<>();
            ApiFuture<QuerySnapshot> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION)
                    .whereEqualTo(NAME_FIELD, name).get();
            for (DocumentSnapshot doc : future.get().getDocuments()) {
                Deck deck = doc.toObject(Deck.class);
                if (deck != null) {
                    deck.deckId(doc.getId());
                }
                deckList.add(deck);
            }
            return deckList;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String addCardIdToDeckById(String id, String cardId) {
        try {
            if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()){
                ApiFuture<WriteResult> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id)
                        .update(CARD_ID_FIELD, FieldValue.arrayUnion(cardId));
                return future.get().getUpdateTime().toString();
            } else {
                return "";
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteCardIdFromDeckById(String id, String cardId) {
        try {
            if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()){
                ApiFuture<WriteResult> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id)
                        .update(CARD_ID_FIELD, FieldValue.arrayRemove(cardId));
                return future.get().getUpdateTime().toString();
            } else {
                return "";
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String updateDeckNameById(String id, String name) {
        try {
            if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()){
                Map<String, Object> updates = new HashMap<>();
                updates.put(NAME_FIELD, name);
                ApiFuture<WriteResult> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id)
                        .update(updates);
                return future.get().getUpdateTime().toString();
            } else {
                return "";
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteDeckById(String id) {
        try {
            if (firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).get().get().exists()){
                ApiFuture<WriteResult> future = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION).document(id).delete();
                return future.get().getUpdateTime().toString();
            } else {
                return "";
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String,String>> deleteDeckByName(String name) {
        try {
            List<Map<String,String>> deleteList = new ArrayList<>();
            ApiFuture<QuerySnapshot> querySnapshot = firestore.collection(PATH_NAME_FOR_DECK_COLLECTION)
                    .whereEqualTo(NAME_FIELD, name).get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            if (documents.isEmpty()) {
                return null;
            }
            for (QueryDocumentSnapshot doc : documents) {
                ApiFuture<WriteResult> deleteFuture = doc.getReference().delete();
                Map<String,String> map = Map.of(doc.getId(), deleteFuture.get().getUpdateTime().toString());
                deleteList.add(map);
            }
            return deleteList;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
