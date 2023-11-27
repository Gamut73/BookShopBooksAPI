package com.artificery.BobShopBooksAPI.controller;

import com.artificery.BobShopBooksAPI.dto.BookInfoFilterDto;
import com.artificery.BobShopBooksAPI.service.SearchCacheService;
import com.artificery.BobShopBooksAPI.utility.BookInfoFilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("search/cache/")
public class SearchCacheController {

    private final SearchCacheService searchCacheService;

    @PostMapping("{cacheFileName}")
    public ResponseEntity<?> getCacheFile(@PathVariable String cacheFileName, @RequestBody List<BookInfoFilterDto> filters) {
        return new ResponseEntity<>(searchCacheService.retrieveCachedSearchAndFilter(cacheFileName, filters), HttpStatus.OK);
    }

    @GetMapping("list")
    public ResponseEntity<?> getAllMySavedCaches() {
        return new ResponseEntity<>(searchCacheService.getAllMySavedCaches(), HttpStatus.OK);
    }
}
