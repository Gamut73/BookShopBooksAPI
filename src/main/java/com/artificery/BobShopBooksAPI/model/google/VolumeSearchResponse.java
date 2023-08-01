package com.artificery.BobShopBooksAPI.model.google;

import java.util.List;
import lombok.Data;

@Data
public class VolumeSearchResponse {
	private String totalItems;
	private List<Volume> items;
}
