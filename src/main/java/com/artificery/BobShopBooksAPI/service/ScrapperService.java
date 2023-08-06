package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import com.artificery.BobShopBooksAPI.model.TheStoryGraphBookDetail;
import com.artificery.BobShopBooksAPI.model.google.IndustryIdentifiersItem;
import com.artificery.BobShopBooksAPI.model.google.Volume;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import com.artificery.BobShopBooksAPI.model.google.VolumeSearchResponse;
import com.opencsv.exceptions.CsvValidationException;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScrapperService {

    private final CSVService csvService;
    private final BookInfoService bookInfoService;
    private WebDriver webDriver;

    private final Map<String, List<VolumeInfo>> bookInfoForSeller = new HashMap<>();

    ScrapperService(CSVService csvService, BookInfoService bookInfoService) {
        this.csvService = csvService;
        this.bookInfoService = bookInfoService;
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver_win32/chromedriver.exe");
    }

    public List<VolumeInfo> getBookDetailsFromSeller(String sellerId) {
        List<BobStoreBookInfo> bookTitles = scrapBookTitlesFromSeller(sellerId);
        List<VolumeInfo>  bookInfo = new ArrayList<>();

        for (BobStoreBookInfo bookTitle : bookTitles) {
            Optional.ofNullable(bookInfoService.searchForBook(bookTitle.getListingTitle()))
                    .map(VolumeSearchResponse::getItems)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(Volume::getVolumeInfo)
                    .peek(this::addLinksForStoryGraphAndGoodreads)
                    .findFirst()
                    .ifPresent(bookInfo::add);
        }

        return bookInfo;
    }

    public List<VolumeInfo> compareSellerBooksToMyToReadList(String sellerId) throws IOException, CsvValidationException {
        List<VolumeInfo> volumeInfos = getSellerBooksInfo(sellerId);

        List<TheStoryGraphBookDetail> toReadList = getToReadBookList();

        return volumeInfos.stream().filter(volumeInfo -> toReadList.stream().anyMatch(
                    toReadBook -> isSameBook(volumeInfo, toReadBook))).collect(Collectors.toList());
    }

    private void addLinksForStoryGraphAndGoodreads(VolumeInfo volumeInfo) {
        Optional.ofNullable(volumeInfo.getIndustryIdentifiers())
                .stream()
                .flatMap(Collection::stream)
                .filter(identifier -> identifier.getType().contains("ISBN_13"))
                .findFirst()
                .ifPresent(identifier -> {
                    String url = new StringBuilder("https://www.goodreads.com/search?utf8=%E2%9C%93&query=")
                            .append(identifier.getIdentifier())
                            .toString();
                    volumeInfo.setGoodReadsPreviewLink(url);
                });
        Optional.ofNullable(volumeInfo.getTitle())
                        .ifPresent(title -> volumeInfo.setStorygraphSearchLink("https://app.thestorygraph.com/browse?search_term=" + title.replace(" ", "+")));
    }

    private List<VolumeInfo> getSellerBooksInfo(String sellerId) {

        List<BobStoreBookInfo> currSellerBookTitles = scrapBookTitlesFromSeller(sellerId);


        if (!bookInfoForSeller.containsKey(sellerId)) {
            bookInfoForSeller.put(sellerId, new ArrayList<>());

            bookInfoForSeller.get(sellerId).addAll(getBookSellerInfo(currSellerBookTitles.stream()
                    .map(BobStoreBookInfo::getListingTitle)
                    .collect(Collectors.toList())));

            return bookInfoForSeller.get(sellerId);
        }

        List<String> cachedBookTitles = bookInfoForSeller.get(sellerId).stream()
                .map(VolumeInfo::getTitle)
                .collect(Collectors.toList());

        //Remove books the seller is no longer listing
        cachedBookTitles
                .stream()
                .filter(cachedTitle -> currSellerBookTitles.stream().noneMatch(book -> cachedTitle.toLowerCase().contains(book.getListingTitle().toLowerCase())))
                .forEach(title -> bookInfoForSeller.get(sellerId).removeIf(volume -> title.toLowerCase().contains(volume.getTitle().toLowerCase())));

        //Get info for new books
        List<String> newSellerTitles = currSellerBookTitles
                .stream()
                .map(BobStoreBookInfo::getListingTitle)
                .filter(listingTitle -> cachedBookTitles.stream().noneMatch(cachedTitle -> listingTitle.toLowerCase().contains(cachedTitle.toLowerCase())))
                .collect(Collectors.toList());


        bookInfoForSeller.get(sellerId).addAll(getBookSellerInfo(newSellerTitles));

        return bookInfoForSeller.get(sellerId);
    }

    private List<VolumeInfo> getBookSellerInfo(List<String> bookTitles) {
        return bookInfoService.searchForBooks(bookTitles)
                .stream()
                .filter(volumeSearchResponse -> volumeSearchResponse.getItems() != null && volumeSearchResponse.getItems().size() > 0 && volumeSearchResponse.getItems().get(0) != null)
                .map(volumeSearchResponse -> volumeSearchResponse.getItems().get(0).getVolumeInfo())
                .peek(volumeInfo -> volumeInfo.setStorygraphSearchLink("https://app.thestorygraph.com/browse?search_term=" + volumeInfo.getTitle()))
                .peek(volumeInfo -> volumeInfo.getIndustryIdentifiers()
                        .stream()
                        .filter(industryIdentifier -> industryIdentifier.getType().equalsIgnoreCase("ISBN_10"))
                        .findFirst()
                        .ifPresent(identifier -> {
                            String url = new StringBuilder("https://www.goodreads.com/search?utf8=%E2%9C%93&query=")
                                    .append(identifier.getIdentifier())
                                    .toString();
                            volumeInfo.setGoodReadsPreviewLink(url);
                        }))
                .collect(Collectors.toList());
    }

    private boolean isSameBook(VolumeInfo volumeInfo, TheStoryGraphBookDetail theStoryGraphBookDetail) {

        List<IndustryIdentifiersItem> volumeIdentifiers = volumeInfo.getIndustryIdentifiers();
        return volumeIdentifiers != null && volumeIdentifiers.stream().anyMatch(id -> compareNullableStrings(id.getIdentifier(), theStoryGraphBookDetail.getIdentifier()) ||
                compareNullableStrings(volumeInfo.getTitle(), theStoryGraphBookDetail.getTitle()) ||
                theStoryGraphBookDetail.getTitle().toLowerCase().contains(volumeInfo.getTitle().toLowerCase()));
    }

    private boolean compareNullableStrings(String string1, String string2) {
        return string1 != null && string1.equalsIgnoreCase(string2);
    }

    private List<BobStoreBookInfo> scrapBookTitlesFromSeller(String sellerId) {
        List<BobStoreBookInfo> bookTitles = new ArrayList<>();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);

        webDriver = getChromeWebDriver();
        webDriver.get("https://www.bobshop.co.za/seller/" + sellerId);


        String bookCategoryXPath = "//li[@class='filter-list-item']/a[contains(@onclick, \"BobeTradelist.setInputAndSubmit('CategoryId',105);\")]";
        WebElement booksAndEducationLink = webDriver.findElement(By.partialLinkText("Books"));
//        new WebDriverWait(webDriver, Duration.ofMinutes(2)).until(ExpectedConditions.elementToBeClickable(By.xpath(bookCategoryXPath)));
//
//        booksAndEducationLink.click();

        JavascriptExecutor js = (JavascriptExecutor)webDriver;
        js.executeScript("arguments[0].click()", booksAndEducationLink);

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

        return bookTitles;
    }

    private WebElement getNextButton() {
        try {
            return webDriver.findElement(By.xpath("//span[text()='Next']"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private List<TheStoryGraphBookDetail> getToReadBookList() throws CsvValidationException, IOException {
        List<String[]> toReadBooks = csvService.readCSVFile("./to_read.csv");

        return toReadBooks.stream()
                .map(row -> new TheStoryGraphBookDetail(row[0], row[1], row[2]))
                .collect(Collectors.toList());
    }

    private WebDriver getChromeWebDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(chromeOptions);
    }
}
