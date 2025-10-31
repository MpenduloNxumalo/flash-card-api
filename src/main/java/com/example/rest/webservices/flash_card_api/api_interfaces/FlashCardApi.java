package com.example.rest.webservices.flash_card_api.api_interfaces;

import com.example.rest.webservices.flash_card_api.api.*;

public interface FlashCardApi extends CreateFlashCardApi, RetrieveAllFlashCardsApi,
    RetrieveFlashCardByIdApi, RetrieveFlashCardByNameApi, RetrieveAllFlashCardsInDeckApi,
    UpdateFlashCardTitleByIdApi, UpdateFlashCardContentByIdApi, DeleteFlashCardByIdApi {
}
