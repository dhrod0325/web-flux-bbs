package web.flux.bbs.domain.post.entity;


import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("post")
@Data
public class Post {
    @Id
    private Long id;

    private String title;
    private String content;
    private String author;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}