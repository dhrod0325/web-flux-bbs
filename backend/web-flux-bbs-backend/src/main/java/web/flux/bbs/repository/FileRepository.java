package web.flux.bbs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.File;

public interface FileRepository extends ReactiveCrudRepository<File, Long> {
    Mono<File> findFileByPostIdAndId(Long postId, Long fileId);
}