package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BOBBookShoppingService {

    private final ScrapperService scrapperService;
    private final BookInfoService bookInfoService;

    public List<VolumeInfo> getBOBSellerBooksInfo(String sellerId) {
        return scrapperService.scrapBookTitlesFromSeller(sellerId)
                .stream()
                .map(BobStoreBookInfo::getListingTitle)
                .map(bookInfoService::getBookInformation)
                .collect(Collectors.toList());
    }
}
