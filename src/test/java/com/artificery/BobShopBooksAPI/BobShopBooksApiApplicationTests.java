package com.artificery.BobShopBooksAPI;

import com.artificery.BobShopBooksAPI.dto.BookInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

@SpringBootTest
class BobShopBooksApiApplicationTests {

	@Test
	void contextLoads() {
		BookInfoDto bookInfoDto = new BookInfoDto();
		bookInfoDto.setAverageRating("4.5");
		bookInfoDto.setAuthors(List.of("Pathetic McIdiotson"));

		ExpressionParser expressionParser = new SpelExpressionParser();
		Expression expression = expressionParser.parseExpression("authors");

		EvaluationContext context = new StandardEvaluationContext(bookInfoDto);
		Object fieldToEvaluate = (Object) expression.getValue(context);

		List<String> authorsList = (List<String>) fieldToEvaluate;

		EvaluationContext listContext = new StandardEvaluationContext(authorsList);
		List<String> filterList = (List<String>) expressionParser.parseExpression("#authorsList.?[#this.matches('.*' + idiot + '.*')]").getValue(listContext);
		Boolean filterResult = (Boolean) filterList.isEmpty();
	}

}
