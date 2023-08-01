package com.artificery.BobShopBooksAPI.controller;

import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import com.artificery.BobShopBooksAPI.service.ScrapperService;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("seller")
@RequiredArgsConstructor
public class HelloController {

    private final ScrapperService scrapperService;

    @GetMapping("/{sellerId}/books/to/read/compare")
    public List<VolumeInfo> openBrowser(@PathVariable String sellerId) throws IOException, CsvValidationException {
        return scrapperService.compareSellerBooksToMyToReadList(sellerId);
    }

    @GetMapping("/{sellerId}/books/info")
    public List<VolumeInfo> getSellerBooksInfo(@PathVariable String sellerId) {
        return scrapperService.getBookDetailsFromSeller(sellerId);
    }
}
