package web.flux.bbs.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import web.flux.bbs.controller.post.PostController;
import web.flux.bbs.dto.PostDto;
import web.flux.bbs.service.PostService;

@WebFluxTest(PostController.class)
class PostControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PostService postService;

    @Test
    void createPost_shouldReturnCreatedPost_whenValidRequest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");

        Mono<PostDto> postDtoMono = Mono.just(postDto);
        Mockito.when(postService.createPost(Mockito.any(PostDto.class))).thenReturn(postDtoMono);

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
}