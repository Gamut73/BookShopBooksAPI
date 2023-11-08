package com.artificery.BobShopBooksAPI.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookSellerInfoRequestDTO {
    private List<String> categories;
}
