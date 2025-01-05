package web.flux.bbs.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.flux.bbs.dto.CommentDto;
import web.flux.bbs.dto.FileDto;
import web.flux.bbs.dto.PagedResponse;
import web.flux.bbs.dto.PostDto;
import web.flux.bbs.service.PostService;

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

    @PostMapping("/{postId}/upload")
    public Mono<FileDto> uploadFileToPost(
            @PathVariable Long postId,
            @RequestPart("file") Mono<FilePart> filePartMono) {
        return postService.uploadFileToPost(postId, filePartMono);
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

    @PostMapping("/{postId}/comments")
    public Mono<ResponseEntity<CommentDto>> addComment(@PathVariable Long postId, @Valid @RequestBody CommentDto commentDto) {
        return postService.addCommentToPost(postId, commentDto)
                .map(createdComment -> ResponseEntity.created(URI.create("/api/posts/" + postId + "/comments/" + createdComment.getId()))
                        .body(createdComment))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public Mono<ResponseEntity<CommentDto>> updateComment(@PathVariable Long postId, @PathVariable Long commentId,
                                                          @Valid @RequestBody CommentDto commentDto) {
        return postService.updateComment(postId, commentId, commentDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public Mono<ResponseEntity<Object>> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return
                postService.deleteComment(postId, commentId)
                        .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()))
                        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{postId}/comments")
    public Flux<CommentDto> getComments(@PathVariable Long postId) {
        log.info("Fetching all comments for post with ID: {}", postId);

        return postService.getCommentsByPostId(postId);
    }

    @GetMapping("/{postId}/download/{fileId}")
    public Mono<ResponseEntity<FileDto>> downloadFileFromPost(@PathVariable Long postId, @PathVariable Long fileId) {
        return postService.downloadFileFromPost(postId, fileId)
                .map(fileDto -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + fileDto.getFileName() + "\"")
                        .body(fileDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}