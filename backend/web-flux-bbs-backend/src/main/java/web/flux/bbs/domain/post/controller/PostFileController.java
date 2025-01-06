package web.flux.bbs.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import web.flux.bbs.domain.post.dto.FileDto;
import web.flux.bbs.domain.post.service.PostFileService;

@RequiredArgsConstructor
@RestController
public class PostFileController {
    private final PostFileService postFileService;

    @PostMapping("/{postId}/upload")
    public Mono<FileDto> uploadFileToPost(
            @PathVariable Long postId,
            @RequestPart("file") Mono<FilePart> filePartMono) {
        return postFileService.uploadFileToPost(postId, filePartMono);
    }

    @GetMapping("/{postId}/download/{fileId}")
    public Mono<ResponseEntity<FileDto>> downloadFileFromPost(@PathVariable Long postId, @PathVariable Long fileId) {
        return postFileService.downloadFileFromPost(postId, fileId)
                .map(fileDto -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + fileDto.getFileName() + "\"")
                        .body(fileDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
