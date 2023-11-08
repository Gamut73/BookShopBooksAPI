package com.artificery.BobShopBooksAPI.controller;

import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import com.artificery.BobShopBooksAPI.service.BOBBookShoppingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("seller")
@RequiredArgsConstructor
public class BobShopBookShoppingInfoController {

    private final BOBBookShoppingService bookShoppingService;

    @GetMapping("/{sellerId}/books/info")
    public List<VolumeInfo> getSellerBooksInfo(@PathVariable String sellerId) {
        return bookShoppingService.getBOBSellerBooksInfo(sellerId);
    }

}
