package team9.demo.error;



import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ErrorCode {
    // Auth errors
    WRONG_VERIFICATION_CODE("AUTH_1", "인증 번호가 틀렸습니다."),
    EXPIRED_VERIFICATION_CODE("AUTH_2", "인증 번호가 만료되었습니다."),
    TOKEN_EXPIRED("AUTH_3", "토큰이 만료되었습니다."),
    INVALID_TOKEN("AUTH_4", "토큰을 확인해주세요"),
    NOT_AUTHORIZED("AUTH_5", "인증되지 않았습니다."),
    PHONE_NUMBER_IS_USED("AUTH_5", "해당 전화번호로 이미 다른 사람이 사용중입니다."),
    EMAIL_ADDRESS_IS_USED("AUTH_6", "해당 이메일로 이미 다른 사람이 사용중입니다."),
    EMAIL_NOT_FOUND("AUTH_7", "해당 이메일을 찾을 수 없습니다."),
    PHONE_NUMBER_NOT_FOUND("AUTH_8", "해당 전화번호를 찾을 수 없습니다."),
    WRONG_PASSWORD("AUTH_9", "비밀번호가 틀렸습니다."),
    INVALID_PHONE_NUMBER("AUTH_10", "잘못된 전화번호입니다."),
    ACCESS_DENIED("AUTH_11", "접근 권한이 없습니다."),

    // Common
    PATH_WRONG("COMMON_1", "잘못된 메세드입니다."),
    VARIABLE_WRONG("COMMON_2", "요청 변수가 잘못되었습니다."),
    WRONG_ACCESS("COMMON_3", "잘못된 접근입니다."),
    INTERNAL_SERVER_ERROR("COMMON_4", "Internal Server Error"),
    //파일 관련
    FILE_UPLOAD_FAILED("FILE_1", "파일 업로드를 실패하였습니다."),
    FILE_DELETE_FAILED("FILE_2", "파일 삭제를 실패하였습니다."),
    FILE_CONVERT_FAILED("FILE_3", "파일 변환에 실패하였습니다."),
    FILE_NAME_COULD_NOT_EMPTY("FILE_4", "파일 이름이 없습니다"),
    NOT_SUPPORT_FILE_TYPE("FILE_5", "지원하지 않는 형식의 파일입니다."),
    FILE_NAME_INCORRECT("FILE_6", "파일 이름이 잘못되었습니다."),



    USER_NOT_FOUND("USER_1", "회원을 찾을 수 없음."),
    USER_NOT_ACCESS("USER_2", "사용자가 활성화되지 않았습니다."),
    USER_ALREADY_CREATED("USER_3", "이미 가입된 사용자입니다."),
    USER_NOT_CREATED("USER_4", "가입되지 않은 사용자입니다."),
    USER_PUSH_TOKEN_NOT_FOUND("USER_5", "푸시 토큰을 찾을 수 없음."),

    FRIEND_NOT_FOUND("FRIEND_1", "친구를 찾을 수 없음."),
    FRIEND_ALREADY_CREATED("FRIEND_2", "이미 추가된 친구입니다."),
    FRIEND_MYSELF("FRIEND_3", "자기 자신을 친구로 추가할 수 없습니다."),
    FRIEND_BLOCK("FRIEND_4", "차단한 친구입니다."),
    FRIEND_BLOCKED("FRIEND_5", "차단당한 친구입니다."),
    FRIEND_DELETED("FRIEND_6", "삭제된 친구입니다."),
    FRIEND_NORMAL("FRIEND_7", "일반 사용자입니다."),

    FEED_NOT_FOUND("FEED_1", "피드를 찾을 수 없음."),
    FEED_IS_NOT_OWNED("FEED_4", "피드 작성자가 아닙니다."),
    FEED_IS_OWNED("FEED_5", "피드 작성자입니다."),
    FEED_IS_NOT_VISIBLE("FEED_6", "피드를 볼 수 없습니다."),




    INVALID_TYPE("INVALID_1", "잘못된 타입입니다."),


    // 미션 관련
    MISSION_NOT_FOUND("MISSION_1", "미션이 존재하지 않습니다."),
    TODAY_MISSION_LIMIT_EXCEEDED("MISSION_2", "오늘의 미션은 최대 3개까지 등록할 수 있습니다."),

    //AI 관련
    AI_PROMPT_FAILED("AI_1", "AI 프롬프트를 실패하였습니다. 응답이 완전하지 않습니다."),
    AI_NOTIFICATION_NOT_SUPPORTED("AI_2", "AI 알림을 지원하지 않습니다."),
    AI_NOT_SUPPORTED("AI_4", "AI를 지원하지 않습니다."),
    REST_CLIENT_EXCEPTION("AI_5", "OpenAI 호출 중 RestClientException 발생"),
    AI_QUOTA_EXCEEDED("AI_6", "OpenAI 호출 실패 - 요금제 초과"),
    AI_RATE_LIMIT_EXCEEDED("AI_7", "OpenAI 호출 실패 - 속도 제한 초과"),
    AI_REQUEST_TOO_LARGE("AI_8", "요청이 너무 커서 처리할 수 없습니다."),
    AI_IMAGE_GENERATED_FAILED("AI_9", "이미지 생성에 실패하였습니다."),
    AI_MASK_FAILED("AI_10", "마스크 생성에 실패하였습니다."),
    AI_IMAGE_SIZE_ANALYSIS_FAILED("AI_11", "이미지 크기 분석에 실패하였습니다."),
    AI_IMAGE_LAMA_FAILED("AI_12", "lama-cleaner 호출 실패");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private static final Map<String, ErrorCode> ERROR_CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(ErrorCode::getMessage, Function.identity()));

    public static ErrorCode from(String message) {
        return ERROR_CODE_MAP.getOrDefault(message, INTERNAL_SERVER_ERROR);
    }
}

