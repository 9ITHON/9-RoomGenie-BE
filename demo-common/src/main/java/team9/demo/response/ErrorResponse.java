package team9.demo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.error.ErrorCode;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String errorCode;
    private final String message;

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}