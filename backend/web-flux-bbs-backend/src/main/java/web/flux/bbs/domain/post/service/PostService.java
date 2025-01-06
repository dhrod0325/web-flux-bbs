package web.flux.bbs.domain.post.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.entity.Post;
import web.flux.bbs.domain.post.dto.PagedResponse;
import web.flux.bbs.domain.post.dto.PostDto;
import web.flux.bbs.domain.post.mapper.PostMapper;
import web.flux.bbs.domain.post.repository.PostRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;

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
        return postRepository
                .findById(id)
                .flatMap(existingPost -> {
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