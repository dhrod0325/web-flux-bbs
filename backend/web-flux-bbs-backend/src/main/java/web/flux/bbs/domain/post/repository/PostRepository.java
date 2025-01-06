package web.flux.bbs.domain.post.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import web.flux.bbs.domain.post.entity.Post;

public interface PostRepository extends ReactiveCrudRepository<Post, Long> {
}
