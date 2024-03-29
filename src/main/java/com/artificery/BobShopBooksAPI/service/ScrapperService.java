package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScrapperService {

    private WebDriver webDriver;

    ScrapperService() {
        System.setProperty("webdriver.chrome.driver", "chrome_driver/chromedriver.exe");
    }

    public List<BobStoreBookInfo> scrapBookTitlesFromSellerByCategory(String sellerId, List<String> categories) {

        webDriver = getChromeWebDriver();
        webDriver.get("https://www.bobshop.co.za/seller/" + sellerId);

        goToSellersBooks(webDriver);

        getIntoCategories(webDriver, categories);

        List<BobStoreBookInfo> bookTitles = scrapPages();

        webDriver.quit();

        return bookTitles;
    }

    public List<BobStoreBookInfo> scrapBookTitlesFromSeller(String sellerId) {

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);

        webDriver = getChromeWebDriver();
        webDriver.get("https://www.bobshop.co.za/seller/" + sellerId);

        goToSellersBooks(webDriver);

        List<BobStoreBookInfo> bookTitles = scrapPages();

        webDriver.quit();

        return bookTitles;
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
            elements.addAll(webDriver.findElements(By.className("tradelist-item-main-link")));
            elements.forEach(element -> {
                String bookItemPageLink = element.getAttribute("href");
                String bookTitle = element.getAttribute("title");
                bookTitles.add(new BobStoreBookInfo(bookTitle, bookItemPageLink));
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

    private WebDriver getChromeWebDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--remote-allow-origins=*");

        return new ChromeDriver(chromeOptions);
    }
}
