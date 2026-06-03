package rmsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespondDTO {

    private String statusCode;

    private String message;

    private Object data;

    private Integer total;

    private Object error;
}
