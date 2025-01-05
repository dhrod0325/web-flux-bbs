package web.flux.bbs.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
    private long totalElements; // 총 게시글 수
    private List<T> content;    // 데이터 리스트
}