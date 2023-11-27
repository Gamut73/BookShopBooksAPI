package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.utility.BookInfoFilterUtil;
import com.artificery.BobShopBooksAPI.utility.SearchCacheFileUtil;
import com.artificery.BobShopBooksAPI.dto.BookInfoDto;
import com.artificery.BobShopBooksAPI.dto.BookInfoFilterDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchCacheService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<BookInfoDto> retrieveCachedSearchAndFilter(String cachedSearchFileName, List<BookInfoFilterDto> filters) {
        String searchResultsAsString = SearchCacheFileUtil.readFileFromSearchCache(cachedSearchFileName);

        List<BookInfoDto> bookInfoDtoList = convertJsonToBookInfoDtoList(searchResultsAsString);


        return BookInfoFilterUtil.applyFiltersOnBookList(bookInfoDtoList, filters);
    }

    public List<BookInfoDto> retrieveCachedSearch(String cachedSearchFileName) {
        String searchResultsAsString = SearchCacheFileUtil.readFileFromSearchCache(cachedSearchFileName);

        return convertJsonToBookInfoDtoList(searchResultsAsString);
    }

    public List<String> getAllMySavedCaches() {
        return SearchCacheFileUtil.listFilesInSearchCacheFolder();
    }

    public void saveSearch(String sellerId, String category, List<BookInfoDto> bookInfoDtoList) {
        String searchResultsAsJson = convertSearchObjectToString(bookInfoDtoList);

        SearchCacheFileUtil.saveJsonStringToFile(searchResultsAsJson, sellerId, category);
    }

    private List<BookInfoDto> convertJsonToBookInfoDtoList(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<BookInfoDto>>() {});
        } catch (IOException e) {
            // Handle the exception (e.g., log or throw)
            e.printStackTrace();
            return null;
        }
    }

    private String convertSearchObjectToString(List<BookInfoDto> bookInfoDtoList) {
        try {
            return objectMapper.writeValueAsString(bookInfoDtoList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert bookInfoList to Json", e);
        }
    }

}
