package com.example.rest.webservices.flash_card_api.repositories;

import com.example.rest.webservices.flash_card_api.models.FlashCard;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import io.grpc.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashCardRepositoryTests {
    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private ApiFuture<DocumentReference> apiFuture;

    @Mock
    private DocumentReference documentReference;

    @InjectMocks
    private FlashCardRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFlashCardWhenFlashCardArgIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repository.createFlashCard(null);
        });

        assertEquals("Flash card argument is null", exception.getMessage());
    }

    @Test
    void testCreateFlashCardSuccessfullyCase() throws ExecutionException, InterruptedException {
        FlashCard flashCard = new FlashCard().front("front").back("back");

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.add(flashCard)).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("abc123");

        String result = repository.createFlashCard(flashCard);

        assertEquals("abc123", result);
        verify(firestore).collection(anyString());
        verify(collectionReference).add(flashCard);
    }

    @Test
    void testCreateFlashCardThrowsFirestoreException() throws Exception {
        FlashCard flashCard = new FlashCard().front("front").back("back");
        FirestoreException firestoreEx = mock(FirestoreException.class);
        ExecutionException execEx = new ExecutionException(firestoreEx);

        when(firestoreEx.getCode()).thenReturn(Status.ABORTED.getCode().value());
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.add(flashCard)).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(execEx);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.createFlashCard(flashCard));

        assertTrue(exception.getMessage().contains("Firestore error:"));
    }

    @Test
    void testCreateFlashCardThrowsInterruptedException() throws Exception {
        FlashCard flashCard = new FlashCard().front("front").back("back");

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.add(flashCard)).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new InterruptedException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.createFlashCard(flashCard));

        assertTrue(exception.getMessage().contains("Thread interrupted while fetching flashcards"));
    }
}