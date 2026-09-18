package rmsbackend.dto.table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import rmsbackend.enums.TableStatus;

@Getter
@Setter
@Builder
public class TableScanResponse {

    private String id;
    private String tableNumber;
    private Integer capacity;
    private String location;
    private TableStatus status;
    private boolean orderingEnabled;
    private String message;

}
