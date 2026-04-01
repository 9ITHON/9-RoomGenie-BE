package team9.demo.controller;

import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import team9.demo.RestDocsTest;
import team9.demo.TestUserArgumentResolver;
import team9.demo.controller.user.UserController;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.user.UserId;
import team9.demo.service.user.UserService;
import team9.demo.util.handler.GlobalExceptionHandler;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static team9.demo.RestDocsUtils.*;

@ActiveProfiles("test")
public class UserControllerTest extends RestDocsTest {

    private UserService userService;

    @BeforeEach
    void setUpController() {
        userService = mock(UserService.class);
        UserController controller = new UserController(userService);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = mockControllerWithAdviceAndCustomConverter(
                controller, exceptionHandler, null, new TestUserArgumentResolver()
        );
    }

    @Test
    @DisplayName("사용자 검색 - 성공")
    void searchUser() {
        UserId targetId = UserId.of("foundUserId");
        when(userService.searchUser("테스트유저")).thenReturn(targetId);

        ValidatableMockMvcResponse response = given()
                .header("Authorization", "Bearer testToken")
                .param("name", "테스트유저")
                .get("/api/users/search")
                .then();

        response.statusCode(200)
                .body("status", equalTo(200))
                .body("data.userId", equalTo("foundUserId"))
                .body("data.userName", equalTo("테스트유저"))
                .apply(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        queryParameters(
                                parameterWithName("name").description("검색할 사용자 이름")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.userId").description("검색된 사용자 ID"),
                                fieldWithPath("data.userName").description("검색된 사용자 이름")
                        )
                ));

        verify(userService, times(1)).searchUser("테스트유저");
    }

    @Test
    @DisplayName("사용자 검색 - 실패 (존재하지 않는 사용자)")
    void searchUserNotFound() {
        when(userService.searchUser(anyString()))
                .thenThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND));

        ValidatableMockMvcResponse response = given()
                .header("Authorization", "Bearer testToken")
                .param("name", "없는유저")
                .get("/api/users/search")
                .then();

        assertErrorResponse(response, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)
                .apply(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        responseErrorFields(
                                HttpStatus.NOT_FOUND,
                                ErrorCode.USER_NOT_FOUND,
                                "해당 이름의 사용자가 존재하지 않는 경우"
                        )
                ));
    }
}
