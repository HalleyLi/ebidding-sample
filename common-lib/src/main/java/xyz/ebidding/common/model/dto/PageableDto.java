package xyz.ebidding.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageableDto<T> {
    private List<T> rows;

    private Integer limit;
    private Integer offset;
    private Long totalElements;
    private Integer totalPages;
}
