package web.flux.bbs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import web.flux.bbs.domain.Comment;
import web.flux.bbs.dto.CommentDto;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDto toDto(Comment vo);

    Comment toEntity(CommentDto dto);
}