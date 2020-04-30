package response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StatusResponse {
    SUCCESS ("Success"),
    ERROR ("Error");

    @Getter
    private String status;
}
