package rmsbackend.common.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String code;

    private String message;

    private Boolean success;

    private T data;

    private Instant timestamp;

}
