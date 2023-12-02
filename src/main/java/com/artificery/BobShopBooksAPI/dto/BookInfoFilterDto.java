package com.artificery.BobShopBooksAPI.dto;

import com.artificery.BobShopBooksAPI.enums.BookFilterType;
import com.artificery.BobShopBooksAPI.enums.FilterField;
import com.artificery.BobShopBooksAPI.enums.FilterOperator;
import lombok.Data;

@Data
public class BookInfoFilterDto {
    private FilterField name;
    private BookFilterType type;
    private String value;
    private FilterOperator operator;
}
