package com.artificery.BobShopBooksAPI.dto;

import com.artificery.BobShopBooksAPI.enums.BookFilterType;
import com.artificery.BobShopBooksAPI.enums.FilterField;
import lombok.Data;

@Data
public class BookInfoFilterDto {
    private FilterField fieldName;
    private BookFilterType filterType;
    private String value;
}
