package com.artificery.BobShopBooksAPI.utility;

import com.artificery.BobShopBooksAPI.dto.BookInfoDto;
import com.artificery.BobShopBooksAPI.dto.BookInfoFilterDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BookInfoFilterUtil {

    private BookInfoFilterUtil() {

    }

    public static List<BookInfoDto> applyFiltersOnBookList(List<BookInfoDto> bookInfoDtos, List<BookInfoFilterDto> filters) {
        List<BookInfoDto> filteredBookInfos = bookInfoDtos;

        for (BookInfoFilterDto filter: filters) {
            filteredBookInfos = applyFilter(filteredBookInfos, filter);
        }

        return filteredBookInfos;
    }

    private static List<BookInfoDto> applyFilter(List<BookInfoDto> bookInfoDtos, BookInfoFilterDto filter) {
        switch (filter.getFieldName()) {
            case RATING:
                return filterByRatings(bookInfoDtos, filter);
            case RATINGS_COUNT:
                return filterByRatingsCount(bookInfoDtos, filter);
            case AUTHOR:
                return filterByAuthor(bookInfoDtos, filter);
            case CATEGORIES:
                return filterByCategories(bookInfoDtos, filter);
            default:
                return bookInfoDtos;
        }
    }

    private static List<BookInfoDto> filterByCategories(List<BookInfoDto> bookInfoDtos, BookInfoFilterDto filter) {
        switch (filter.getFilterType()) {
            case CONTAINS:
                return bookInfoDtos
                        .stream()
                        .filter(bookInfo -> bookInfo.getCategories() != null && !bookInfo.getCategories().isEmpty() && bookInfo.getCategories().stream().anyMatch(category -> category.toLowerCase().contains(filter.getValue().toLowerCase())))
                        .collect(Collectors.toList());
            case NOT_CONTAINS:
                return bookInfoDtos
                        .stream()
                        .filter(bookInfo ->  {
                            if (bookInfo.getCategories() != null && !bookInfo.getCategories().isEmpty()) {
                                return !bookInfo.getCategories().stream().anyMatch(category -> category.equalsIgnoreCase(filter.getValue()));
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            default:
                log.warn("The scenario for the following filter hasn't been handled yet: {}", filter);
                return bookInfoDtos;
        }
    }

    private static List<BookInfoDto> filterByAuthor(List<BookInfoDto> bookInfoDtos, BookInfoFilterDto filter) {
        switch (filter.getFilterType()) {
            case CONTAINS:
                return bookInfoDtos
                        .stream()
                        .filter(bookInfo -> bookInfo.getAuthors() != null && !bookInfo.getAuthors().isEmpty() && bookInfo.getAuthors().stream().anyMatch(author -> author.toLowerCase().contains(filter.getValue().toLowerCase())))
                        .collect(Collectors.toList());
            case NOT_CONTAINS:
                return bookInfoDtos
                        .stream()
                        .filter(bookInfo ->  {
                            if (bookInfo.getAuthors() != null && !bookInfo.getAuthors().isEmpty()) {
                                return !bookInfo.getAuthors().stream().anyMatch(author -> author.equalsIgnoreCase(filter.getValue()));
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            default:
                log.warn("The scenario for the following filter hasn't been handled yet: {}", filter);
                return bookInfoDtos;
        }
    }

    private static List<BookInfoDto> filterByRatings(List<BookInfoDto> bookInfoDtos, BookInfoFilterDto filter) {
        switch (filter.getFilterType()) {
            case GREATER_OR_EQUAL_TO:
                return bookInfoDtos
                        .stream()
                        .filter(bookInfo -> bookInfo.getAverageRating() != null && Double.valueOf(bookInfo.getAverageRating()) >= Double.valueOf(filter.getValue()))
                        .collect(Collectors.toList());

            case IS_NOT_NULL:
                return bookInfoDtos
                        .stream()
                        .filter(bookInfo -> bookInfo.getAverageRating() != null)
                        .collect(Collectors.toList());
            default:
               log.warn("The scenario for the following filter hasn't been handled yet: {}", filter);
               return bookInfoDtos;
        }
    }

    private static List<BookInfoDto> filterByRatingsCount(List<BookInfoDto> bookInfoDtos, BookInfoFilterDto filter) {
        switch (filter.getFilterType()) {
            case GREATER_OR_EQUAL_TO:
                return bookInfoDtos
                        .stream()
                        .filter(bookInfo -> bookInfo.getRatingsCount() != null && Integer.valueOf(bookInfo.getRatingsCount()) >= Integer.valueOf(filter.getValue()))
                        .collect(Collectors.toList());
            default:
                log.warn("The scenario for the following filter hasn't been handled yet: {}", filter);
                return bookInfoDtos;
        }
    }
}
