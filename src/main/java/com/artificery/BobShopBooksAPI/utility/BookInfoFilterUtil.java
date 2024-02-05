package com.artificery.BobShopBooksAPI.utility;

import com.artificery.BobShopBooksAPI.dto.BookInfoDto;
import com.artificery.BobShopBooksAPI.dto.BookInfoFilterDto;
import com.artificery.BobShopBooksAPI.enums.FilterField;
import com.artificery.BobShopBooksAPI.enums.FilterOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BookInfoFilterUtil {

    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    private BookInfoFilterUtil() {

    }

    public static List<BookInfoDto> applyFiltersOnBookList(List<BookInfoDto> bookInfoDtos, List<BookInfoFilterDto> filters) {
        List<BookInfoDto> filteredBookInfos = bookInfoDtos;

        for (BookInfoFilterDto filter: filters) {
            filteredBookInfos = applyFilterGeneric(filteredBookInfos, filter);
        }

        return filteredBookInfos;
    }

    private static List<BookInfoDto> applyFilterGeneric(List<BookInfoDto> bookInfos, BookInfoFilterDto filter) {
        return bookInfos
                .stream()
                .filter(bookInfo -> evaluateFilter(bookInfo, filter)).collect(Collectors.toList());
    }

    private static Boolean evaluateFilter(BookInfoDto bookInfo, BookInfoFilterDto filter) {
        Expression expression = expressionParser.parseExpression(getFieldNameFromFilterField(filter.getName()));

        EvaluationContext context = new StandardEvaluationContext(bookInfo);
        Object fieldToEvaluate = expression.getValue(context);

        if (fieldToEvaluate == null) {
            return false;
        } else if (fieldToEvaluate instanceof List) {
            return evaluateFilterForListField((List<String>) fieldToEvaluate, filter.getValue(), filter.getOperator());
        } else {
            return evaluateFilterForStringField(filter, (String) fieldToEvaluate);
        }
    }

    private static Boolean evaluateFilterForStringField(BookInfoFilterDto filter, String fieldToEvaluate) {
        if (filter.getOperator() == FilterOperator.CONTAINS) {
            return fieldToEvaluate.toLowerCase().contains(filter.getValue().toLowerCase());
        } else if (filter.getOperator() == FilterOperator.NOT_CONTAINS) {
            return !fieldToEvaluate.toLowerCase().contains(filter.getValue().toLowerCase());
        } else {
            Expression evaluationExpression = expressionParser.parseExpression(fieldToEvaluate + getOperator(filter.getOperator()) + filter.getValue());
            return (Boolean) evaluationExpression.getValue();
        }
    }

    private static Boolean evaluateFilterForListField(List<String> values, String filterValue, FilterOperator operator) {
        switch (operator) {
            case LIST_CONTAINS:
                return values.stream().anyMatch(value -> value.equalsIgnoreCase(filterValue));
            case LIST_CONTAINS_PARTIAL:
                return values.stream().anyMatch(value -> value.toLowerCase().contains(filterValue.toLowerCase()));
            case LIST_NOT_CONTAINS:
                return values.stream().anyMatch(value -> !value.equalsIgnoreCase(filterValue));
            case LIST_NOT_CONTAINS_PARTIAL:
                return values.stream().anyMatch(value -> !value.toLowerCase().contains(filterValue.toLowerCase()));
            default:
                throw new IllegalArgumentException(String.format("Filter Operator: %s is not supported for list filtering", operator.toString()));
        }
    }

    private static String getFieldNameFromFilterField(FilterField filterField) {
        List<String> subWords = new ArrayList<>(Arrays.asList(filterField.toString().toLowerCase().split("_")));
        StringBuilder fieldName = new StringBuilder(subWords.get(0));
        subWords.remove(0);

        for (String word: subWords) {
            fieldName.append(word.replace(word.charAt(0), Character.toUpperCase(word.charAt(0))));
        }

        return fieldName.toString();
    }

    private static String getOperator(FilterOperator filterOperator) {
        switch (filterOperator) {
            case EQUAL:
                return "==";
            case NOT_EQUAL:
                return "!=";
            case GREATER_THAN:
                return ">";
            case SMALLER_THAN:
                return "<";
            case GREATER_OR_EQUAL_TO:
                return ">=";
            case SMALLER_OR_EQUAL_TO:
                return "<=";
            default:
                throw new IllegalArgumentException("Unsupported filter operator: " + filterOperator);
        }
    }
}
