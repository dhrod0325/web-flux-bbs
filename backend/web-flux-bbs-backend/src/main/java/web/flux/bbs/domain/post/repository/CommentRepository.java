package web.flux.bbs.domain.post.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.entity.Comment;

public interface CommentRepository extends ReactiveCrudRepository<Comment, Long> {
    Flux<Comment> findByPostId(Long postId);

    Mono<Comment> findByIdAndPostId(Long commentId, Long postId);
}