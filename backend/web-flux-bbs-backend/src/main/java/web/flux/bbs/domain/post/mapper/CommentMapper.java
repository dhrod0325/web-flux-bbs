package web.flux.bbs.domain.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import web.flux.bbs.domain.post.entity.Comment;
import web.flux.bbs.domain.post.dto.CommentDto;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDto toDto(Comment vo);

    Comment toEntity(CommentDto dto);
}