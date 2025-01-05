package web.flux.bbs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import web.flux.bbs.domain.Post;

public interface PostRepository extends ReactiveCrudRepository<Post, Long> {
}
