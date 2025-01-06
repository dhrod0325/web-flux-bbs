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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.dto.PagedResponse;
import web.flux.bbs.domain.post.dto.PostDto;
import web.flux.bbs.domain.post.service.PostService;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public Mono<PagedResponse<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return postService.getPagedPosts(page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PostDto>> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<PostDto>> createPost(@Valid @RequestBody PostDto postDto) {
        return postService.createPost(postDto)
                .map(createdPost -> ResponseEntity.created(URI.create("/api/posts/" + createdPost.getId()))
                        .body(createdPost));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PostDto>> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto postDto) {
        return postService.updatePost(id, postDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deletePost(@PathVariable Long id) {
        return postService.deletePost(id)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}