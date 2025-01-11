package web.flux.bbs.domain.post.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.dto.CommentDto;
import web.flux.bbs.domain.post.service.PostCommentService;

@WebFluxTest(PostCommentController.class)
public class PostCommentControllerTest {
    @MockitoBean
    private PostCommentService postCommentService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addComment_ShouldReturnCreatedStatus_WhenValidRequest() {
        Long postId = 1L;

        CommentDto inputCommentDto = CommentDto.builder()
                .content("This is a test comment.")
                .build();

        CommentDto createdCommentDto = CommentDto.builder()
                .content("This is a test comment.")
                .id(1L)
                .postId(postId)
                .build();

        when(postCommentService
                .addCommentToPost(postId, inputCommentDto))
                .thenReturn(Mono.just(createdCommentDto));

        webTestClient.post()
                .uri("/api/posts/{id}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inputCommentDto)
                .exchange()
                .expectStatus().isCreated() // HTTP 201 상태 확인
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentDto.class)
                .isEqualTo(createdCommentDto);
    }

    @Test
    void addComment_ShouldReturnNotFoundStatus_WhenServiceReturnsEmpty() {
        Long postId = 1L;

        CommentDto inputCommentDto = CommentDto.builder()
                .content("This is a test comment.")
                .build();

        when(postCommentService.addCommentToPost(postId, inputCommentDto)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/posts/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(inputCommentDto))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void addComment_ShouldReturnBadRequestStatus_WhenInputIsInvalid() {
        long postId = 1L;

        CommentDto invalidCommentDto = CommentDto.builder().build(); // Empty content, considered invalid

        webTestClient.post()
                .uri("/api/posts/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidCommentDto)
                .exchange()
                .expectStatus()
                .is5xxServerError(); // Expects 400 status code
    }
}