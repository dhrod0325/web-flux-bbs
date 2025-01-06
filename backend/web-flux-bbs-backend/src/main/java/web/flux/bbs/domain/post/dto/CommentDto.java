package web.flux.bbs.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long parentId;
    private Long postId;

    private List<CommentDto> children;

    private String content;
    private String author;
    private LocalDateTime createdAt;
}