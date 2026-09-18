package rmsbackend.dto.table;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TableSummaryResponse {

    private long totalTables;
    private long available;
    private long occupied;
    private long reserved;
    private long cleaning;
    private long inactive;

}
