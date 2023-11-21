package com.artificery.BobShopBooksAPI.mapper;

import com.artificery.BobShopBooksAPI.dto.BookInfoDto;
import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GoogleVolumeInfoMapper {

    BookInfoDto mapVolumeInfoToBookInfo(VolumeInfo volumeInfo);

    default List<BookInfoDto> mapVolumeInfoListToBookInfoList(List<VolumeInfo> volumeInfos) {
        return volumeInfos.stream().map(this::mapVolumeInfoToBookInfo).collect(Collectors.toList());
    }
}
