package web.flux.bbs.domain.post.controller;

import static org.mockito.ArgumentMatchers.anyLong;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.dto.PostDto;
import web.flux.bbs.domain.post.service.PostService;

@WebFluxTest(PostController.class)
class PostControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PostService postService;

    @Test
    void createPost_shouldReturnCreatedPost_whenValidRequest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");

        Mono<PostDto> postDtoMono = Mono.just(postDto);
        Mockito.when(postService.createPost(Mockito.any(PostDto.class)))
                .thenReturn(postDtoMono);

        webTestClient.post()
                .uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/posts/1")
                .expectBody(PostDto.class)
                .isEqualTo(postDto);
    }

    @Test
    void getPostById_shouldReturnPost_whenPostExists() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");

        Mockito.when(postService.getPostById(anyLong()))
                .thenReturn(Mono.just(postDto));

        webTestClient.get()
                .uri("/api/posts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PostDto.class)
                .isEqualTo(postDto);
    }

    @Test
    void deletePost_shouldReturnNoContent_whenPostExists() {
        Mockito.when(postService.deletePost(anyLong()))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/posts/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getPostById_shouldReturnNotFound_whenPostDoesNotExist() {
        Mockito.when(postService.getPostById(anyLong()))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/posts/1")
                .exchange()
                .expectStatus().isNotFound();
    }
}