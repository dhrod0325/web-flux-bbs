package web.flux.bbs.domain.post.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.entity.File;

public interface FileRepository extends ReactiveCrudRepository<File, Long> {
    Mono<File> findFileByPostIdAndId(Long postId, Long fileId);
}