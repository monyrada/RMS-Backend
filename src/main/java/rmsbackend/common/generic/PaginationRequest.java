package rmsbackend.common.generic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {
    private Integer offset = 0;
    private Integer max = 10;
    private String sort;
    private String order;

    public Integer getMax() {
        return Math.min(max == null ? 10 : max, 100);
    }

    public Integer getOffset() {
        return offset == null ? 0 : offset;
    }

    public String getSort() {
        return (sort == null || sort.isBlank()) ? "id" : sort;
    }

    public String getOrder() {
        return (order == null || order.isBlank())
                ? "asc"
                : order.toLowerCase();
    }
}