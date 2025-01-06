package web.flux.bbs.domain.post.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.dto.CommentDto;
import web.flux.bbs.domain.post.service.PostCommentService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PostMapping("/{postId}/comments")
    public Mono<ResponseEntity<CommentDto>> addComment(@PathVariable Long postId, @Valid @RequestBody CommentDto commentDto) {
        return postCommentService.addCommentToPost(postId, commentDto)
                .map(createdComment -> ResponseEntity.created(URI.create("/api/posts/" + postId + "/comments/" + createdComment.getId()))
                        .body(createdComment))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public Mono<ResponseEntity<CommentDto>> updateComment(@PathVariable Long postId, @PathVariable Long commentId,
                                                          @Valid @RequestBody CommentDto commentDto) {
        return postCommentService.updateComment(postId, commentId, commentDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public Mono<ResponseEntity<Object>> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return
                postCommentService.deleteComment(postId, commentId)
                        .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()))
                        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{postId}/comments")
    public Flux<CommentDto> getComments(@PathVariable Long postId) {
        return postCommentService.getCommentsByPostId(postId);
    }
}
