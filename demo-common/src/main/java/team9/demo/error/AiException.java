package team9.demo.error;

import lombok.Getter;

@Getter
public class AiException extends RuntimeException {

    private final ErrorCode errorCode;

    public AiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}