package team9.demo.error;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final ErrorCode errorCode;

    public ConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 예외 메시지 출력용
        this.errorCode = errorCode;
    }
}