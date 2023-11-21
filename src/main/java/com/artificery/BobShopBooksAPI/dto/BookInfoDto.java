package com.artificery.BobShopBooksAPI.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookInfoDto {
    private String title;
    private String subtitle;
    private String description;
    private List<String> authors;
    private String pageCount;
    private String averageRating;
    private String ratingsCount;
    private String language;
    private String publishedDate;
    private List<String> categories;
    private String goodReadsPreviewLink;
    private String storygraphSearchLink;
    private String bobShopItemPageLink;
}
