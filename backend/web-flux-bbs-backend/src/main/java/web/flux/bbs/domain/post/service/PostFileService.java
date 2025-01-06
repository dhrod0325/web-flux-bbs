package web.flux.bbs.domain.post.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import web.flux.bbs.domain.post.entity.File;
import web.flux.bbs.domain.post.dto.FileDto;
import web.flux.bbs.domain.post.mapper.FileMapper;
import web.flux.bbs.domain.post.repository.FileRepository;
import web.flux.bbs.domain.post.repository.PostRepository;

@RequiredArgsConstructor
@Service
public class PostFileService {
    private final FileRepository fileRepository;

    private final PostRepository postRepository;

    public Mono<FileDto> uploadFileToPost(Long postId, Mono<FilePart> filePartMono) {
        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당 ID의 게시물을 찾을 수 없습니다: " + postId)))
                .flatMap(post -> filePartMono.flatMap(filePart -> {
                    String fileName = UUID.randomUUID() + "_" + filePart.filename();
                    Path destination = Paths.get("uploads/" + fileName);

                    return createDirectoryIfNotExists(destination.getParent())
                            .then(filePart.transferTo(destination))
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

    private Mono<Void> createDirectoryIfNotExists(Path directoryPath) {
        return Mono.fromCallable(() -> {
                    if (Files.notExists(directoryPath)) {
                        Files.createDirectories(directoryPath);
                    }

                    return directoryPath;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<FileDto> downloadFileFromPost(Long postId, Long fileId) {
        return fileRepository.findFileByPostIdAndId(postId, fileId)
                .switchIfEmpty(Mono.error(new RuntimeException("File not found")))
                .map(FileMapper.INSTANCE::toDto);
    }
}
