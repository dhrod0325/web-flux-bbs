package web.flux.bbs.domain.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import web.flux.bbs.domain.post.entity.Post;
import web.flux.bbs.domain.post.dto.PostDto;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostDto toDto(Post vo);

    Post toEntity(PostDto dto);
}