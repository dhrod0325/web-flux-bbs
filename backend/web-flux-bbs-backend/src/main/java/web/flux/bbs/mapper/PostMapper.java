package web.flux.bbs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import web.flux.bbs.domain.Post;
import web.flux.bbs.dto.PostDto;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostDto toDto(Post vo);

    Post toEntity(PostDto dto);
}