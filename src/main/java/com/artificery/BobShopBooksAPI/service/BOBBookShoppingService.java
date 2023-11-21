package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BOBBookShoppingService {

    private final ScrapperService scrapperService;
    private final BookInfoService bookInfoService;

    public List<VolumeInfo> getBOBSellerBooksInfoByCategory(String sellerId, List<String> categories) {

        return scrapperService.scrapBookTitlesFromSellerByCategory(sellerId, categories)
                .stream()
                .map(BobStoreBookInfo::getListingTitle)
                .map(bookInfoService::getBookInformation)
                .collect(Collectors.toList());
    }

    public List<VolumeInfo> getBOBSellerBooksInfo(String sellerId) {
        return scrapperService.scrapBookTitlesFromSeller(sellerId)
                .stream()
                .map(BobStoreBookInfo::getListingTitle)
                .map(bookInfoService::getBookInformation)
                .collect(Collectors.toList());
    }
}
