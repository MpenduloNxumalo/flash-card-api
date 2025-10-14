package com.example.rest.webservices.flash_card_api.api_interfaces;

import com.example.rest.webservices.flash_card_api.api.CreateFlashCardApi;
import com.example.rest.webservices.flash_card_api.api.RetrieveAllFlashCardsApi;
import com.example.rest.webservices.flash_card_api.api.RetrieveFlashCardByIdApi;
import com.example.rest.webservices.flash_card_api.api.RetrieveFlashCardByNameApi;

public interface FlashCardApi extends
        CreateFlashCardApi,
        RetrieveAllFlashCardsApi,
        RetrieveFlashCardByIdApi,
        RetrieveFlashCardByNameApi {
}
