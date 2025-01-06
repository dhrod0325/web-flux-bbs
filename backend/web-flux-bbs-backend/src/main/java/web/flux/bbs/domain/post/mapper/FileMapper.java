package web.flux.bbs.domain.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import web.flux.bbs.domain.post.entity.File;
import web.flux.bbs.domain.post.dto.FileDto;

@Mapper
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    FileDto toDto(File vo);

    File toEntity(FileDto dto);
}