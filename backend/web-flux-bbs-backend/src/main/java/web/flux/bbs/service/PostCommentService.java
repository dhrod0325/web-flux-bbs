package web.flux.bbs.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.Comment;
import web.flux.bbs.dto.CommentDto;
import web.flux.bbs.mapper.CommentMapper;
import web.flux.bbs.repository.CommentRepository;

@RequiredArgsConstructor
@Service
public class PostCommentService {
    private final CommentRepository commentRepository;

    public Mono<CommentDto> addCommentToPost(Long postId, CommentDto commentDto) {
        Comment comment = CommentMapper.INSTANCE.toEntity(commentDto);
        comment.setPostId(postId);

        if (ObjectUtils.isEmpty(comment.getAuthor())) {
            comment.setAuthor("anonymous");
        }

        if (commentDto.getParentId() != null) {
            return commentRepository.findById(commentDto.getParentId())
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid parent comment ID")))
                    .then(commentRepository.save(comment))
                    .map(CommentMapper.INSTANCE::toDto);
        }

        return commentRepository.save(comment).map(CommentMapper.INSTANCE::toDto); // 최상위 댓글
    }

    public Mono<CommentDto> updateComment(Long postId, Long commentId, CommentDto commentDto) {
        return commentRepository.findByIdAndPostId(commentId, postId)
                .flatMap(existingComment -> {
                    existingComment.setContent(commentDto.getContent());
                    existingComment.setUpdatedAt(LocalDateTime.now());
                    return commentRepository.save(existingComment);
                })
                .map(CommentMapper.INSTANCE::toDto);
    }

    public Mono<Void> deleteComment(Long postId, Long commentId) {
        return commentRepository
                .findByIdAndPostId(commentId, postId)
                .flatMap(commentRepository::delete);
    }

    public Flux<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .map(CommentMapper.INSTANCE::toDto)
                .collectList()
                .map(this::buildCommentHierarchy)
                .flatMapMany(Flux::fromIterable);
    }

    private List<CommentDto> buildCommentHierarchy(List<CommentDto> comments) {
        Map<Long, List<CommentDto>> groupedByParentId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getParentId() == null ? -1L : comment.getParentId() // null 값을 -1로 대체
                ));

        List<CommentDto> rootComments = groupedByParentId
                .getOrDefault(-1L, new ArrayList<>());

        rootComments.forEach(rootComment -> populateChildren(rootComment, groupedByParentId));

        return rootComments;
    }

    private void populateChildren(CommentDto parent, Map<Long, List<CommentDto>> groupedByParentId) {
        List<CommentDto> children = groupedByParentId.getOrDefault(parent.getId(), new ArrayList<>());
        parent.setChildren(children); // children 필드를 추가했다고 가정

        children.forEach(child -> populateChildren(child, groupedByParentId));
    }
}
