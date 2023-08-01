package com.artificery.BobShopBooksAPI.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TheStoryGraphBookDetail {
    private String title;
    private String author;
    private String identifier;
}
