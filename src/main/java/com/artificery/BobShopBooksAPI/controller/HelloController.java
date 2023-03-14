package com.artificery.BobShopBooksAPI.controller;

import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import com.artificery.BobShopBooksAPI.service.NamePendingService;
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

    private final NamePendingService namePendingService;

    @GetMapping("/{sellerId}")
    public List<BobStoreBookInfo> openBrowser(@PathVariable String sellerId) throws IOException, CsvValidationException {
        return namePendingService.getSellerBookList(sellerId);
    }
}
