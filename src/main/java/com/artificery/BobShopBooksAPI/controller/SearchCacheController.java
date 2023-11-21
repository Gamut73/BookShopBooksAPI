package com.artificery.BobShopBooksAPI.controller;

import com.artificery.BobShopBooksAPI.service.SearchCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("search/cache/")
public class SearchCacheController {

    private final SearchCacheService searchCacheService;

    @GetMapping("{cacheFileName}")
    public ResponseEntity<?> getCacheFile(@PathVariable String cacheFileName) {
        return new ResponseEntity<>(searchCacheService.retrieveCachedSearch(cacheFileName), HttpStatus.OK);
    }

    @GetMapping("list")
    public ResponseEntity<?> getAllMySavedCaches() {
        return new ResponseEntity<>(searchCacheService.getAllMySavedCaches(), HttpStatus.OK);
    }
}
