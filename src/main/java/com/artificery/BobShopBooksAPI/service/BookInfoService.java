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
                .peek(this::addLinksForStoryGraphAndGoodreads)
                .findFirst()
                .orElse(null);
    }

    private void addLinksForStoryGraphAndGoodreads(VolumeInfo volumeInfo) {
        Optional.ofNullable(volumeInfo.getIndustryIdentifiers())
                .stream()
                .flatMap(Collection::stream)
                .filter(identifier -> identifier.getType().contains("ISBN_13"))
                .findFirst()
                .ifPresent(identifier -> {
                    String url = new StringBuilder("https://www.goodreads.com/search?utf8=%E2%9C%93&query=")
                            .append(identifier.getIdentifier())
                            .toString();
                    volumeInfo.setGoodReadsPreviewLink(url);
                });
        Optional.ofNullable(volumeInfo.getTitle())
                .ifPresent(title -> volumeInfo.setStorygraphSearchLink("https://app.thestorygraph.com/browse?search_term=" + title.replace(" ", "+")));
    }
}
