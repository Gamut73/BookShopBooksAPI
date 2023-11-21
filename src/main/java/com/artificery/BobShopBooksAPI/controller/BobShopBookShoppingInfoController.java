package com.artificery.BobShopBooksAPI.controller;

import com.artificery.BobShopBooksAPI.dto.BookInfoDto;
import com.artificery.BobShopBooksAPI.dto.BookSellerInfoRequestDTO;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import com.artificery.BobShopBooksAPI.service.BOBBookShoppingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("seller")
@RequiredArgsConstructor
public class BobShopBookShoppingInfoController {

    private final BOBBookShoppingService bookShoppingService;

    @GetMapping("/{sellerId}/books/info")
    public ResponseEntity<?> getSellerBooksInfo(@PathVariable String sellerId) {
        return new ResponseEntity<List<BookInfoDto>>(bookShoppingService.getBOBSellerBooksInfo(sellerId), HttpStatus.OK);
    }

    @PostMapping("/{sellerId}/books/category/info")
    public ResponseEntity<?> getSellerBooksInfoByCategory(@PathVariable String sellerId, @RequestBody BookSellerInfoRequestDTO requestDTO) {
        return new ResponseEntity<List<BookInfoDto>>(bookShoppingService.getBOBSellerBooksInfoByCategory(sellerId, requestDTO.getCategories()), HttpStatus.OK);
    }
}
