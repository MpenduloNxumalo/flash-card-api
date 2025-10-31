package com.example.rest.webservices.flash_card_api.api_interfaces;

import com.example.rest.webservices.flash_card_api.api.*;

public interface DeckApi extends CreateDeckApi, RetrieveAllDecksApi, RetrieveDeckByIdApi,
    RetrieveDeckByNameApi, UpdateDeckNameByIdApi, AddCardIdToDeckByIdApi, DeleteCardIdToDeckByIdApi,
    DeleteDeckByIdApi, DeleteDeckByNameApi {
}
