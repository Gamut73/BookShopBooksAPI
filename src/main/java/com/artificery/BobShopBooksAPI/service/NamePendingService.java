package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import com.artificery.BobShopBooksAPI.model.BookDetail;
import com.opencsv.exceptions.CsvValidationException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NamePendingService {

    private final CSVService csvService;
    private WebDriver webDriver;

    NamePendingService(CSVService csvService) {
        this.csvService = csvService;
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver_win32/chromedriver.exe");
    }

    public List<BobStoreBookInfo> getSellerBookList(String sellerId) throws IOException, CsvValidationException {

        List<BobStoreBookInfo> bookTitles = new ArrayList<>();

        webDriver = getChromeWebDriver();
        webDriver.get("https://www.bobshop.co.za/seller/" + sellerId);

        WebElement booksAndEducationLink = webDriver.findElement(By.partialLinkText("Books"));
        booksAndEducationLink.click();

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

        webDriver.quit();

        csvService.saveObjectsToCSV(bookTitles, String.format("./%s_book_list.csv", sellerId));

        List<BookDetail> toReadList = getToReadBookList();

        return bookTitles.stream().filter(bookTitle -> !toReadList.stream().noneMatch(
                    toReadBook -> bookTitle.getListingTitle().contains(toReadBook.getTitle()))
                ).collect(Collectors.toList());
    }

    private WebElement getNextButton() {
        try {
            return webDriver.findElement(By.xpath("//span[text()='Next']"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private List<BookDetail> getToReadBookList() throws CsvValidationException, IOException {
        List<String[]> toReadBooks = csvService.readCSVFile("./to_read.csv");

        return toReadBooks.stream()
                .map(row -> row[0])
                .map(BookDetail::new)
                .collect(Collectors.toList());
    }

    private WebDriver getChromeWebDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(chromeOptions);
    }
}
