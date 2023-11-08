package com.artificery.BobShopBooksAPI;


import com.artificery.BobShopBooksAPI.model.google.VolumeSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookInfoRestClient {

    WebClient webClient;

    BookInfoRestClient() {
        webClient = getWebClient();
    }

    private final String VOLUME_INFO_FIELDS = "(title,subtitle,authors,publishedDate,description,industryIdentifiers,averageRating,categories,pageCount,ratingsCount,language)";
    private final String SEARCH_FIELDS = "items(id,volumeInfo" + VOLUME_INFO_FIELDS + ")";

    public VolumeSearchResponse searchForBook(String title) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", title)
                        .queryParam("maxResults", 1)
                        .queryParam("fields", SEARCH_FIELDS)
                        .build())
                .retrieve()
                .bodyToMono(VolumeSearchResponse.class)
                .block();
    }

    public List<VolumeSearchResponse> searchForBooks(List<String> titles) {
        List<Mono<VolumeSearchResponse>> responses = new ArrayList<>();

        titles.forEach(title -> responses.add(
                webClient.get().uri("/volumes?q=" + title)
                        .retrieve()
                        .bodyToMono(VolumeSearchResponse.class)
        ));

        return Flux.merge(responses).collectList().block();
    }


    public String getBookInfo(String isbn) {
        return webClient.get()
                .uri("/volumes?q=isbn:" + isbn)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
    }
}
