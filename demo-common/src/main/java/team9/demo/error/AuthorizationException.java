package team9.demo.error;

import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 예외 메시지로 ErrorCode 메시지 사용
        this.errorCode = errorCode;
    }

}