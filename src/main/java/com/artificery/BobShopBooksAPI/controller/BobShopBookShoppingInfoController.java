package com.artificery.BobShopBooksAPI.controller;

import com.artificery.BobShopBooksAPI.dto.BookSellerInfoRequestDTO;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import com.artificery.BobShopBooksAPI.service.BOBBookShoppingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{sellerId}/books/category/info")
    public List<VolumeInfo> getSellerBooksInfoByCategory(@PathVariable String sellerId, @RequestBody BookSellerInfoRequestDTO requestDTO) {
        return bookShoppingService.getBOBSellerBooksInfoByCategory(sellerId, requestDTO.getCategories());
    }
}
