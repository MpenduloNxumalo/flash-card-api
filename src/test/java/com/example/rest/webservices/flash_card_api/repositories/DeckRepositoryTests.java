package com.example.rest.webservices.flash_card_api.repositories;

import com.example.rest.webservices.flash_card_api.exceptions.NotFoundException;
import com.example.rest.webservices.flash_card_api.models.Deck;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import io.grpc.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeckRepositoryTests {
    @Mock
    private Firestore firestore;
    @Mock
    private CollectionReference collectionReference;
    @Mock
    private Query query;
    @Mock
    private WriteResult writeResult;
    @Mock
    private ApiFuture<DocumentReference> documentReferenceApiFuture;
    @Mock
    private ApiFuture<QuerySnapshot> future;
    @Mock
    private ApiFuture<WriteResult> writeFuture;
    @Mock
    private ApiFuture<DocumentSnapshot> apiFuture;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;
    @Mock
    private DocumentSnapshot documentSnapshot;
    @InjectMocks
    private DeckRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDeckWhenDeckArgIsNullCase(){
        RuntimeException exception = assertThrows(RuntimeException.class, () ->{repository.createDeck(null);});
        assertEquals("Deck argument is null",exception.getMessage());
    }

    @Test
    void testCreateDeckSuccessCase() throws ExecutionException, InterruptedException {
        String testDeckId = "Test Deck Id";
        Deck deck = new Deck().deckId(testDeckId);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.add(deck)).thenReturn(documentReferenceApiFuture);
        when(documentReferenceApiFuture.get()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn(deck.getDeckId());

        String result = repository.createDeck(deck);

        assertEquals(testDeckId, result);
        verify(firestore).collection(anyString());
        verify(collectionReference).add(deck);
    }

    @Test
    void testCreateDeckThrowsFirestoreExceptionCase() throws Exception {
        String testDeckId = "Test Deck Id";
        Deck deck = new Deck().deckId(testDeckId);

        FirestoreException firestoreEx = mock(FirestoreException.class);
        ExecutionException execEx = new ExecutionException(firestoreEx);

        when(firestoreEx.getCode()).thenReturn(Status.ABORTED.getCode().value());
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.add(deck)).thenReturn(documentReferenceApiFuture);
        when(documentReferenceApiFuture.get()).thenThrow(execEx);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.createDeck(deck));
        assertTrue(exception.getMessage().contains("Firestore error:"));
    }

    @Test
    void testCreateDeckThrowsInterruptedExceptionCase() throws Exception {
        String testDeckId = "Test Deck Id";
        Deck deck = new Deck().deckId(testDeckId);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.add(deck)).thenReturn(documentReferenceApiFuture);
        when(documentReferenceApiFuture.get()).thenThrow(new InterruptedException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.createDeck(deck));
        assertTrue(exception.getMessage().contains("Thread interrupted while creating decks"));
    }

    @Test
    void testRetrieveAllDecksSuccessCase() throws Exception {
        String testDeckId = "Test Deck Id";
        String testDeckName = "Test Deck Name";
        Deck deck = new Deck().deckId(testDeckId).name(testDeckName);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(List.of(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Deck.class)).thenReturn(deck);
        when(queryDocumentSnapshot.getId()).thenReturn(testDeckId);

        List<Deck> result = repository.retrieveAllDecks();

        assertEquals(1, result.size());
        assertEquals(testDeckName, result.get(0).getName());
        assertEquals(testDeckId, result.get(0).getDeckId());
        verify(firestore).collection(anyString());
    }

    @Test
    void testRetrieveAllDecksExecutionExceptionCase() throws Exception {
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(future);
        when(future.get()).thenThrow(new ExecutionException(new RuntimeException("Firestore failure")));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.retrieveAllDecks());
        assertTrue(exception.getMessage().contains("Error fetching Deck"));
    }

    @Test
    void testRetrieveAllDecksInterruptedExceptionCase() throws Exception {
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(future);
        when(future.get()).thenThrow(new InterruptedException("Thread interrupted"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.retrieveAllDecks());
        assertTrue(exception.getMessage().contains("Thread interrupted"));
        assertTrue(Thread.interrupted());
    }

    @Test
    void testRetrieveDeckByIdSuccessCase() throws Exception {
        String testDeckId = "Test Deck Id";
        Deck expectedDeck = new Deck().deckId(testDeckId);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(testDeckId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Deck.class)).thenReturn(expectedDeck);
        when(documentSnapshot.getId()).thenReturn(testDeckId);

        Deck result = repository.retrieveDeckById(testDeckId);

        assertNotNull(result);
        assertEquals(testDeckId, result.getDeckId());
        verify(firestore, times(2)).collection(anyString());
        verify(collectionReference, times(2)).document(testDeckId);
        verify(documentReference, times(2)).get();
    }

    @Test
    void testRetrieveDeckByIdNotFoundExceptionCase() throws Exception {
        String testDeckId = "nonexistent";
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(testDeckId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        assertThrows(NotFoundException.class, () -> repository.retrieveDeckById(testDeckId));

        verify(documentReference).get();
    }

    @Test
    void testRetrieveDeckByIdExecutionExceptionCase() throws Exception {
        String testDeckId = "deck123";
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(testDeckId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new ExecutionException("Firestore error", new Throwable()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.retrieveDeckById(testDeckId));
        assertTrue(ex.getMessage().contains("Error fetching Deck"));
    }

    @Test
    void testRetrieveDeckByNameSuccessCase() throws Exception {
        String testDeckName = "Test Deck";
        String testDeckId = "Test Deck Id";
        Deck deck = new Deck().name(testDeckName);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo(eq("name"), eq(testDeckName))).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Deck.class)).thenReturn(deck);
        when(queryDocumentSnapshot.getId()).thenReturn(testDeckId);

        List<Deck> result = repository.retrieveDeckByName(testDeckName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeckId, result.get(0).getDeckId());
    }

    @Test
    void testRetrieveDeckByNameNotFoundExceptionCase() throws Exception {
        String testDeckName = "Test Deck Name";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo(eq("name"), eq(testDeckName))).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());


        NotFoundException ex = assertThrows(NotFoundException.class, () -> repository.retrieveDeckByName(testDeckName));
        assertTrue(ex.getMessage().contains(testDeckName));
    }

    @Test
    void testRetrieveDeckByNameExecutionExceptionCase() throws Exception {
        String deckName = "TestDeck";
        ExecutionException executionException = new ExecutionException("Firestore error", new Throwable());

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo(eq("name"), eq(deckName))).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenThrow(executionException);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.retrieveDeckByName(deckName));
        assertTrue(ex.getMessage().contains("Error fetching decks"));
    }

    @Test
    void testAddCardIdToDeckByIdSuccessCase() throws Exception {
        String testDeckId = "deck123";
        String testCardId = "card456";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(testDeckId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentReference.update(eq("cardIds"), any())).thenReturn(writeFuture);
        when(writeFuture.get()).thenReturn(writeResult);
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String result = repository.addCardIdToDeckById(testDeckId, testCardId);

        assertNotNull(result);
        verify(documentReference).update(eq("cardIds"), any());
        verify(writeFuture).get();
    }

    @Test
    void testAddCardIdToDeckByIdDeckNotFoundCase() throws Exception {
        String deckId = "nonexistentDeck";
        String cardId = "card123";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(deckId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        assertThrows(NotFoundException.class, () -> repository.addCardIdToDeckById(deckId, cardId));
    }

    @Test
    void testAddCardIdToDeckByIdExecutionExceptionCase() throws Exception {
        String deckId = "deck123";
        String cardId = "card123";

        ExecutionException executionException = new ExecutionException("Firestore error", new Throwable());

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(deckId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(executionException);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.addCardIdToDeckById(deckId, cardId));
        assertTrue(ex.getMessage().contains("Error updating deck"));
    }

    @Test
    void testAddCardIdToDeckByIdCancellationException() throws CancellationException {
        String deckId = "deck123";
        String cardId = "card123";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(deckId)).thenReturn(documentReference);
        when(documentReference.get()).thenThrow(new CancellationException());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.addCardIdToDeckById(deckId, cardId));

        assertEquals("Computation was cancelled", ex.getMessage());
    }

    @Test
    void testDeleteCardIdFromDeckByIdSuccessCase() throws Exception {
        String id = "deck123";
        String cardId = "cardABC";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(eq(id))).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentReference.update(eq("cardIds"), any())).thenReturn(writeFuture);
        when(writeFuture.get()).thenReturn(writeResult);
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String result = repository.deleteCardIdFromDeckById(id, cardId);

        assertNotNull(result);
        verify(documentReference).update(eq("cardIds"), any(FieldValue.class));
    }

    @Test
    void testDeleteCardIdFromDeckByIdDeckNotFoundCase() throws Exception {
        String id = "missingDeck";
        String cardId = "cardXYZ";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(eq(id))).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> repository.deleteCardIdFromDeckById(id, cardId));

        assertTrue(ex.getMessage().contains(id));
        verify(documentReference, never()).update(anyString(), any());
    }

    @Test
    void testDeleteCardIdFromDeckByIdFirestoreExceptionCase() throws Exception {
        String id = "deck123";
        String cardId = "cardABC";
        FirestoreException firestoreException = mock(FirestoreException.class);
        ExecutionException execEx = new ExecutionException("Firestore error", firestoreException);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(eq(id))).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(firestoreException.getCode()).thenReturn(FirestoreException.forInvalidArgument("",new Object()).getCode());
        when(apiFuture.get()).thenThrow(execEx);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.deleteCardIdFromDeckById(id, cardId));

        assertTrue(ex.getMessage().contains("Firestore error"));
    }

    @Test
    void testDeleteCardIdFromDeckByIdInterruptedExceptionCase() throws Exception {
        String id = "deck123";
        String cardId = "cardABC";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(eq(id))).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new InterruptedException("interrupted"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.deleteCardIdFromDeckById(id, cardId));

        assertTrue(ex.getMessage().contains("Thread interrupted while updating decks"));
        assertTrue(Thread.interrupted());
    }

    @Test
    void testDeleteCardIdFromDeckByIdCancellationExceptionCase() throws Exception {
        String id = "deck123";
        String cardId = "cardABC";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(eq(id))).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new CancellationException("cancelled"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.deleteCardIdFromDeckById(id, cardId));

        assertEquals("Computation was cancelled", ex.getMessage());
    }

    @Test
    void testUpdateDeckNameByIdSuccessCase() throws Exception {
        String id = "123";
        String newName = "Updated Deck";
        Map<String, Object> updates = new HashMap<>(){{put("name", newName);}};

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentReference.update(updates)).thenReturn(writeFuture);
        when(writeFuture.get()).thenReturn(writeResult);
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String result = repository.updateDeckNameById(id, newName);

        assertNotNull(result);
        verify(documentReference).update(argThat(map -> newName.equals(map.get("name"))));
    }

    @Test
    void testUpdateDeckNameByIdDeckNotFoundCase() throws Exception {
        String id = "999";
        String newName = "Non-existent Deck";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        assertThrows(NotFoundException.class, () -> repository.updateDeckNameById(id, newName));
    }

    @Test
    void testUpdateDeckNameByIdFirestoreExceptionCase() throws Exception {
        String id = "123";
        String newName = "Error Deck";
        FirestoreException firestoreException = mock(FirestoreException.class);
        ExecutionException execEx = new ExecutionException(firestoreException);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(firestoreException.getCode()).thenReturn(FirestoreException.forInvalidArgument("",new Object()).getCode());
        when(apiFuture.get()).thenThrow(execEx);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.updateDeckNameById(id, newName));

        assertTrue(ex.getMessage().contains("Firestore error: " + firestoreException.getCode()));
    }

    @Test
    void testUpdateDeckNameByIdInterruptedExceptionCase() throws Exception {
        String id = "123";
        String newName = "Interrupted Deck";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new InterruptedException("Thread interrupted"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.updateDeckNameById(id, newName));
        assertTrue(ex.getMessage().contains("Thread interrupted"));
        assertTrue(Thread.interrupted());
    }

    @Test
    void testUpdateDeckNameByIdCancellationException() throws Exception {
        String id = "123";
        String newName = "Cancelled Deck";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new CancellationException("Cancelled"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.updateDeckNameById(id, newName));

        assertEquals("Computation was cancelled", ex.getMessage());
    }

    @Test
    void testDeleteDeckByIdSuccessCase() throws Exception {
        String id = "deck123";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentReference.delete()).thenReturn(writeFuture);
        when(writeFuture.get()).thenReturn(writeResult);
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String result = repository.deleteDeckById(id);

        assertNotNull(result);
        verify(documentReference).delete();
    }

    @Test
    void testDeleteDeckByIdNotFoundCase() throws Exception {
        String id = "nonexistent";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        assertThrows(NotFoundException.class, () -> repository.deleteDeckById(id));
        verify(documentReference, never()).delete();
    }

    @Test
    void testDeleteDeckByIdFirestoreExceptionCase() throws Exception {
        String id = "123";
        FirestoreException firestoreException = mock(FirestoreException.class);
        ExecutionException execEx = new ExecutionException(firestoreException);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(firestoreException.getCode()).thenReturn(FirestoreException.forInvalidArgument("",new Object()).getCode());
        when(apiFuture.get()).thenThrow(execEx);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.deleteDeckById(id));
        assertTrue(ex.getMessage().contains("Firestore error"));
    }

    @Test
    void testDeleteDeckByIdInterruptedExceptionCase() throws Exception {
        String id = "deckInterrupted";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new InterruptedException("Thread interrupted"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.deleteDeckById(id));

        assertTrue(ex.getMessage().contains("Thread interrupted while deleting decks"));
        assertTrue(Thread.interrupted());
    }

    @Test
    void testDeleteDeckByIdCancellationExceptionCase() throws Exception {
        String id = "deckCancelled";

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(id)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new CancellationException("Cancelled"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.deleteDeckById(id));

        assertEquals("Computation was cancelled", ex.getMessage());
    }
}