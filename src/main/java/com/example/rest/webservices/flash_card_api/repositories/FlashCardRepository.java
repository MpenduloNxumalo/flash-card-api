package com.example.rest.webservices.flash_card_api.repositories;

import com.example.rest.webservices.flash_card_api.models.Deck;
import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.example.rest.webservices.flash_card_api.repositories.repository_interfaces.FlashCardRepositoryInterface;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
@AllArgsConstructor
public class FlashCardRepository implements FlashCardRepositoryInterface {
    private Firestore firestore;

    @Override
    public String createFlashCard(FlashCard flashCard) {
        try {
            ApiFuture<DocumentReference> flashCards = firestore.collection(PATH_NAME_FOR_FLASH_CARD_COLLECTION).add(flashCard);
            return flashCards.get().getId();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Deck> retrieveAllFlashCards(){

        return null;
    }
}
