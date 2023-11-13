package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.CategoryService;
import com.artificery.BobShopBooksAPI.entity.Category;
import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScrapperService {

    private final CategoryService categoryService;
    private WebDriver webDriver;

    ScrapperService(CategoryService categoryService) {
        this.categoryService = categoryService;
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver_win32/chromedriver.exe");
    }

    public List<BobStoreBookInfo> scrapBookTitlesFromSellerByCategory(String sellerId, List<String> categories) {
        List<BobStoreBookInfo> bookTitles = new ArrayList<>();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);

        webDriver = getChromeWebDriver();
        webDriver.get("https://www.bobshop.co.za/seller/" + sellerId);

        goToSellersBooks(webDriver);

        getIntoCategories(webDriver, categories);

//        bookTitles = scrapPages();

        webDriver.quit();

        return bookTitles;
    }

    public List<BobStoreBookInfo> scrapBookTitlesFromSeller(String sellerId) {
        List<BobStoreBookInfo> bookTitles = new ArrayList<>();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);

        webDriver = getChromeWebDriver();
        webDriver.get("https://www.bobshop.co.za/seller/" + sellerId);

        goToSellersBooks(webDriver);

        bookTitles = scrapPages();

        webDriver.quit();

        return bookTitles;
    }

    @PostConstruct
    public void initializeCategories() {
        if (!categoryService.categoriesNotYetInitialized()) {
            return;
        }

        Category rootCategory = categoryService.createCategory("Books & Education", 0L);

        buildCategoriesHierarchy(rootCategory)
                .stream()
                .map(Category::getCategoryName)
                .forEach(log::info);
    }

    private void getIntoCategories(WebDriver webDriver, List<String> categories) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        categories.stream()
                .forEach(category -> {
                    WebElement categoryLink = webDriver.findElement(By.partialLinkText(category));
                    js.executeScript("arguments[0].click()", categoryLink);
                });
    }

    private void goToSellersBooks(WebDriver webDriver) {
        WebElement booksAndEducationLink = webDriver.findElement(By.partialLinkText("Books"));

        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].click()", booksAndEducationLink);
    }

    private List<BobStoreBookInfo> scrapPages() {
        List<BobStoreBookInfo> bookTitles = new ArrayList<>();

        WebElement nextButton;

        do {
            List<WebElement> elements = new ArrayList<>();
            elements.addAll(webDriver.findElements(By.className("tradelist-item-title")));
            elements.forEach(element -> {
                bookTitles.add(new BobStoreBookInfo(element.getText()));
            });
            nextButton = getNextButton();
            if (nextButton != null) {
                try {
                    nextButton.click();
                } catch (StaleElementReferenceException e) {
                    nextButton = getNextButton();
                    nextButton.click();
                } catch (ElementClickInterceptedException e) {
                    nextButton = getNextButton();
                    nextButton.click();
                }
            }
        } while (nextButton != null);

        return bookTitles;
    }

    private WebElement getNextButton() {
        try {
            return webDriver.findElement(By.xpath("//span[text()='Next']"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }



    public List<Category> buildCategoriesHierarchy(Category rootCategory) {
        List<Category> categories;

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);

        webDriver = getChromeWebDriver();
        webDriver.get("https://www.bobshop.co.za");

        navigateToBooksPage(webDriver);
        categories = addChildrenForCategory(webDriver, rootCategory, "AllCategories");

        webDriver.quit();

        return categories;
    }

    private List<Category> addChildrenForCategory(WebDriver webDriver, Category category, String parentCategoryName) {
        List<Category> categories = Arrays.asList(category);

        expandCategory(webDriver, category.getCategoryName());
        List<String> childCategoryNames = getChildCategoryNames(webDriver, category.getCategoryName());

        if (childCategoryNames.isEmpty()) {
            expandCategory(webDriver, parentCategoryName);
            return categories;
        }

        childCategoryNames.forEach(childCategoryName -> {
            Category childCategory = categoryService.createCategory(childCategoryName, category.getParentCategoryId());
            categories.addAll(addChildrenForCategory(webDriver, childCategory, parentCategoryName));
        });

        return categories;
    }

    private List<String> getChildCategoryNames(WebDriver webDriver, String currentCategoryName) {

        WebElement currCategory = webDriver.findElement(By.xpath(String.format("//span[@class='active' and text()='%s']", currentCategoryName)));
        String text1 = currCategory.getText();

        if (text1.equalsIgnoreCase("Africana")) {
            return new ArrayList<>();
        }

        String tagName = currCategory.getTagName();

        WebElement parentListItem = webDriver.findElement(By.xpath(String.format("//span[@class='active' and text()='%s']/parent::*", currentCategoryName)));
        String tagName2 = parentListItem.getTagName();
        String text3 = parentListItem.getText();

        WebElement ol;

        try {
            ol = parentListItem.findElements(By.xpath("*"))
                    .stream()
                    .peek(e -> {
                        log.info("tag: {}, text: {}", e.getTagName(), e.getText());
                    })
                    .filter(webElement -> webElement.getTagName().equalsIgnoreCase("ol"))
                    .findFirst()
                    .orElseThrow(java.util.NoSuchElementException::new);
        } catch (NoSuchElementException ex) {
            return List.of();
        }

        String tagName1 = ol.getTagName();
        String text2 = ol.getText();

        List<WebElement> listItemElements = ol.findElements(By.xpath("*"));

        List<String> collect = listItemElements
                .stream()
                .map(WebElement::getText)
                .map(text -> text.split("\\(")[0])
                .map(String::trim)
                .collect(Collectors.toList());

        return collect;
    }

    private void expandCategory(WebDriver webDriver, String categoryName) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;

        String parentXpath = String.format("//a[text()='%s']", categoryName);
        WebElement element = webDriver.findElement(By.xpath(parentXpath));

        js.executeScript("arguments[0].click()", element);
    }

    private void navigateToBooksPage(WebDriver webDriver) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;

        WebElement searchButton = webDriver.findElement(By.className("search-button"));
        js.executeScript("arguments[0].click()", searchButton);
    }

    private WebDriver getChromeWebDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(chromeOptions);
    }
}
