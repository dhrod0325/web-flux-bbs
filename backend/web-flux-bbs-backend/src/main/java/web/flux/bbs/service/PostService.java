package web.flux.bbs.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import web.flux.bbs.domain.Comment;
import web.flux.bbs.domain.File;
import web.flux.bbs.domain.Post;
import web.flux.bbs.dto.CommentDto;
import web.flux.bbs.dto.FileDto;
import web.flux.bbs.dto.PagedResponse;
import web.flux.bbs.dto.PostDto;
import web.flux.bbs.mapper.CommentMapper;
import web.flux.bbs.mapper.FileMapper;
import web.flux.bbs.mapper.PostMapper;
import web.flux.bbs.repository.CommentRepository;
import web.flux.bbs.repository.FileRepository;
import web.flux.bbs.repository.PostRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final FileRepository fileRepository;

    public Flux<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .map(PostMapper.INSTANCE::toDto);
    }

    public Mono<PostDto> getPostById(Long id) {
        return postRepository
                .findById(id)
                .map(PostMapper.INSTANCE::toDto);
    }

    public Mono<PostDto> createPost(PostDto postDto) {
        Post post = PostMapper.INSTANCE.toEntity(postDto);

        if (ObjectUtils.isEmpty(post.getAuthor())) {
            post.setAuthor("anonymous");
        }

        return postRepository
                .save(post)
                .map(PostMapper.INSTANCE::toDto);
    }

    public Mono<PostDto> updatePost(Long id, PostDto postDto) {
        return postRepository.findById(id).flatMap(existingPost -> {
            Post updatedPost = PostMapper.INSTANCE.toEntity(postDto);
            updatedPost.setId(existingPost.getId());

            return postRepository
                    .save(updatedPost)
                    .map(PostMapper.INSTANCE::toDto);
        });
    }

    public Mono<Void> deletePost(Long id) {
        return postRepository.deleteById(id);
    }

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
                .collect(Collectors.groupingBy(comment ->
                        comment.getParentId() == null ? -1L : comment.getParentId() // null 값을 -1로 대체
                ));

        List<CommentDto> rootComments = groupedByParentId.getOrDefault(-1L, new ArrayList<>()); // 최상위 댓글은 -1L
        for (CommentDto rootComment : rootComments) {
            populateChildren(rootComment, groupedByParentId);
        }

        return rootComments;
    }

    private void populateChildren(CommentDto parent, Map<Long, List<CommentDto>> groupedByParentId) {
        List<CommentDto> children = groupedByParentId.getOrDefault(parent.getId(), new ArrayList<>());
        parent.setChildren(children); // children 필드를 추가했다고 가정
        for (CommentDto child : children) {
            populateChildren(child, groupedByParentId);
        }
    }

    public Mono<FileDto> uploadFileToPost(Long postId, Mono<FilePart> filePartMono) {
        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당 ID의 게시물을 찾을 수 없습니다: " + postId)))
                .flatMap(_ -> filePartMono.flatMap(filePart -> {
                    String fileName = UUID.randomUUID() + "_" + filePart.filename();
                    Path destination = Paths.get("uploads/" + fileName);

                    // 비블로킹 방식으로 디렉토리 생성
                    return ensureDirectoryExists(destination.getParent())
                            .then(filePart.transferTo(destination)) // `transferTo` 자체는 Reactive 방식
                            .then(Mono.defer(() -> {
                                File file = new File();
                                file.setPostId(postId);
                                file.setFileName(fileName);
                                file.setFilePath(destination.toString());
                                return fileRepository.save(file)
                                        .map(FileMapper.INSTANCE::toDto);
                            }));
                }));
    }

    private Mono<Void> ensureDirectoryExists(Path directoryPath) {
        return Mono.fromCallable(() -> {
                    if (Files.notExists(directoryPath)) {
                        Files.createDirectories(directoryPath);
                    }
                    return directoryPath;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<PagedResponse<PostDto>> getPagedPosts(int page, int size, String sortBy, String direction) {
        return getAllPosts()
                .collectList()
                .flatMap(posts -> {
                    int skipCount = page * size;
                    Comparator<PostDto> comparator = getComparator(sortBy, direction);

                    List<PostDto> paginatedPosts = posts.stream()
                            .sorted(comparator)
                            .skip(skipCount)
                            .limit(size)
                            .toList();

                    return Mono.just(new PagedResponse<>(posts.size(), paginatedPosts));
                });
    }

    public Mono<FileDto> downloadFileFromPost(Long postId, Long fileId) {
        return fileRepository.findFileByPostIdAndId(postId, fileId)
                .switchIfEmpty(Mono.error(new RuntimeException("File not found")))
                .map(FileMapper.INSTANCE::toDto);
    }

    private Comparator<PostDto> getComparator(String sortBy, String direction) {
        Comparator<PostDto> comparator = switch (sortBy.toLowerCase()) {
            case "title" -> Comparator.comparing(PostDto::getTitle);
            case "author" -> Comparator.comparing(PostDto::getAuthor);
            case "created_at" -> Comparator.comparing(PostDto::getCreatedAt);
            case "updated_at" -> Comparator.comparing(PostDto::getUpdatedAt);
            default -> Comparator.comparing(PostDto::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}