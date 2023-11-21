package com.artificery.BobShopBooksAPI.service;

import com.artificery.BobShopBooksAPI.dto.BookInfoDto;
import com.artificery.BobShopBooksAPI.mapper.GoogleVolumeInfoMapper;
import com.artificery.BobShopBooksAPI.model.BobStoreBookInfo;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BOBBookShoppingService {

    private final ScrapperService scrapperService;
    private final BookInfoService bookInfoService;
    private final GoogleVolumeInfoMapper googleVolumeInfoMapper;

    public List<BookInfoDto> getBOBSellerBooksInfoByCategory(String sellerId, List<String> categories) {

        return searchScrappedBookTitles(scrapperService.scrapBookTitlesFromSellerByCategory(sellerId, categories));
    }

    public List<BookInfoDto> getBOBSellerBooksInfo(String sellerId) {
        return searchScrappedBookTitles(scrapperService.scrapBookTitlesFromSeller(sellerId));
    }

    private List<BookInfoDto> searchScrappedBookTitles(List<BobStoreBookInfo> bobStoreBookInfoList) {
        List<BookInfoDto> bookInfoList = new ArrayList<>();

        for (BobStoreBookInfo bobStoreBook : bobStoreBookInfoList) {
            VolumeInfo googleVolumeInfo = bookInfoService.getBookInformation(bobStoreBook.getListingTitle());

            BookInfoDto bookInfo = googleVolumeInfoMapper.mapVolumeInfoToBookInfo(googleVolumeInfo);
            bookInfo.setBobShopItemPageLink(bobStoreBook.getBobShopItemPageLink());
            addLinksForStoryGraphAndGoodreads(googleVolumeInfo, bookInfo);

            bookInfoList.add(bookInfo);
        }

        return bookInfoList;
    }

    private void addLinksForStoryGraphAndGoodreads(VolumeInfo volumeInfo, BookInfoDto bookInfoDto) {
        Optional.ofNullable(volumeInfo.getIndustryIdentifiers())
                .stream()
                .flatMap(Collection::stream)
                .filter(identifier -> identifier.getType().contains("ISBN_13"))
                .findFirst()
                .ifPresent(identifier -> {
                    String url = new StringBuilder("https://www.goodreads.com/search?utf8=%E2%9C%93&query=")
                            .append(identifier.getIdentifier())
                            .toString();
                    bookInfoDto.setGoodReadsPreviewLink(url);
                });
        Optional.ofNullable(volumeInfo.getTitle())
                .ifPresent(title -> bookInfoDto.setStorygraphSearchLink("https://app.thestorygraph.com/browse?search_term=" + title.replace(" ", "+")));
    }
}
