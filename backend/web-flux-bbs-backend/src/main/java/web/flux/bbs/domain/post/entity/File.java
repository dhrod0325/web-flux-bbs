package web.flux.bbs.domain.post.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("file")
public class File {
    @Id
    private Long id;

    private Long postId; // 글 ID와 연결
    private String fileName;
    private String filePath;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}