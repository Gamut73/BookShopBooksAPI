package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.BookInfoRestClient;
import com.artificery.BobShopBooksAPI.model.google.Volume;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import com.artificery.BobShopBooksAPI.model.google.VolumeSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookInfoService {

    private final BookInfoRestClient bookInfoRestClient;

    public VolumeInfo getBookInformation(String bookTitle) {
        return Optional.of(bookInfoRestClient.searchForBook(bookTitle))
                .map(VolumeSearchResponse::getItems)
                .stream()
                .flatMap(Collection::stream)
                .map(Volume::getVolumeInfo)
                .findFirst()
                .orElse(null);
    }


}
