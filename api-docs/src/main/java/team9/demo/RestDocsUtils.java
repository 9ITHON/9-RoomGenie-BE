package team9.demo;


import org.springframework.http.HttpStatus;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import team9.demo.error.ErrorCode;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class RestDocsUtils {

    public static OperationRequestPreprocessor requestPreprocessor() {
        return Preprocessors.preprocessRequest(
                Preprocessors.modifyUris()
                        .scheme("http")
                        .host("localhost")
                        .port(8080) // 명시적 포트 포함
                        .removePort(),
                Preprocessors.prettyPrint()
        );
    }

    public static OperationResponsePreprocessor responsePreprocessor() {
        return Preprocessors.preprocessResponse(Preprocessors.prettyPrint());
    }

    public static ResponseFieldsSnippet responseSuccessFields() {
        return responseFields(
                fieldWithPath("status").description("상태 코드"),
                fieldWithPath("data.message").description("성공 메시지")
        );
    }

    public static ResponseFieldsSnippet responseErrorFields(HttpStatus status, ErrorCode errorCode, String description) {
        return responseFields(
                fieldWithPath("status").description(String.valueOf(status.value())),
                fieldWithPath("data.errorCode").description(errorCode.getCode()),
                fieldWithPath("data.message").description(errorCode.getMessage() + " - " + description)
        );
    }

    public static RequestHeadersSnippet requestAccessTokenFields() {
        return requestHeaders(
                headerWithName("Authorization").description("Bearer 액세스 토큰")
        );
    }

    public static RequestHeadersSnippet requestRefreshTokenFields() {
        return requestHeaders(
                headerWithName("Authorization").description("Bearer 리프레시 토큰")
        );
    }
}
