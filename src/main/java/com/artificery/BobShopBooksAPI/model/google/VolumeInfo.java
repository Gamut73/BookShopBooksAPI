package com.artificery.BobShopBooksAPI.model.google;

import java.util.List;
import lombok.Data;

@Data
public class VolumeInfo{
	private List<IndustryIdentifiersItem> industryIdentifiers;
	private String pageCount;
	private String description;
	private String language;
	private String title;
	private String subtitle;
	private String averageRating;
	private String ratingsCount;
	private String publishedDate;
	private List<String> categories;
	private List<String> authors;
	private String goodReadsPreviewLink;
	private String storygraphSearchLink;
}
