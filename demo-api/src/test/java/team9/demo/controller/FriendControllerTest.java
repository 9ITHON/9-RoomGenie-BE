package team9.demo.controller;

import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import team9.demo.RestDocsTest;
import team9.demo.TestUserArgumentResolver;
import team9.demo.controller.friend.FriendController;
import team9.demo.dto.request.friend.FriendRequest;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.facade.friend.FriendFacade;
import team9.demo.model.friend.Friend;
import team9.demo.model.friend.FriendShipStatus;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.User;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.service.friend.FriendShipService;
import team9.demo.util.handler.GlobalExceptionHandler;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static team9.demo.RestDocsUtils.*;

@ActiveProfiles("test")
public class FriendControllerTest extends RestDocsTest {

    private FriendShipService friendShipService;
    private FriendFacade friendFacade;

    @BeforeEach
    void setUpController() {
        friendShipService = mock(FriendShipService.class);
        friendFacade = mock(FriendFacade.class);
        FriendController controller = new FriendController(friendShipService, friendFacade);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = mockControllerWithAdviceAndCustomConverter(
                controller, exceptionHandler, null, new TestUserArgumentResolver()
        );
    }

    @Test
    @DisplayName("친구 추가 - 성공")
    void createFriend() {
        FriendRequest.Create request = new FriendRequest.Create("targetUserId");
        doNothing().when(friendFacade).createFriend(any(UserId.class), any(UserId.class));

        ValidatableMockMvcResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer testToken")
                .body(request)
                .post("/api/friend")
                .then();

        assertCommonSuccessResponse(response)
                .apply(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestFields(
                                fieldWithPath("targetId").description("친구 추가할 대상 사용자 ID")
                        ),
                        responseSuccessFields()
                ));

        verify(friendFacade, times(1)).createFriend(any(UserId.class), any(UserId.class));
    }

    @Test
    @DisplayName("친구 추가 - 실패 (이미 친구)")
    void createFriendAlreadyExists() {
        FriendRequest.Create request = new FriendRequest.Create("targetUserId");
        doThrow(new ConflictException(ErrorCode.FRIEND_ALREADY_CREATED))
                .when(friendFacade).createFriend(any(UserId.class), any(UserId.class));

        ValidatableMockMvcResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer testToken")
                .body(request)
                .post("/api/friend")
                .then();

        assertErrorResponse(response, HttpStatus.CONFLICT, ErrorCode.FRIEND_ALREADY_CREATED)
                .apply(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        responseErrorFields(
                                HttpStatus.CONFLICT,
                                ErrorCode.FRIEND_ALREADY_CREATED,
                                "이미 친구인 사용자에게 다시 요청한 경우"
                        )
                ));
    }

    @Test
    @DisplayName("친구 삭제 - 성공")
    void deleteFriend() {
        FriendRequest.Delete request = new FriendRequest.Delete("targetUserId");
        doNothing().when(friendFacade).deleteFriend(any(UserId.class), any(UserId.class));

        ValidatableMockMvcResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer testToken")
                .body(request)
                .delete("/api/friend")
                .then();

        assertCommonSuccessResponse(response)
                .apply(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestFields(
                                fieldWithPath("targetId").description("친구 삭제할 대상 사용자 ID")
                        ),
                        responseSuccessFields()
                ));

        verify(friendFacade, times(1)).deleteFriend(any(UserId.class), any(UserId.class));
    }

    @Test
    @DisplayName("친구 목록 조회 - 성공")
    void getFriends() {
        UserInfo friendInfo = UserInfo.of(
                UserId.of("friendId1"), "친구1", "01011112222",
                "friend@test.com", "pw", LocalDate.of(2000, 1, 1), AccessStatus.ACCESS
        );
        User friendUser = User.of(friendInfo, "friend@test.com");
        Friend friend = new Friend(friendUser, "친구1", FriendShipStatus.FRIEND);

        when(friendFacade.getFriends(any(UserId.class))).thenReturn(List.of(friend));

        ValidatableMockMvcResponse response = given()
                .header("Authorization", "Bearer testToken")
                .get("/api/friend/list")
                .then();

        response.statusCode(200)
                .body("status", equalTo(200))
                .body("data.friends", hasSize(1))
                .body("data.friends[0].friendId", equalTo("friendId1"))
                .body("data.friends[0].name", equalTo("친구1"))
                .apply(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.friends[]").description("친구 목록"),
                                fieldWithPath("data.friends[].friendId").description("친구 사용자 ID"),
                                fieldWithPath("data.friends[].name").description("친구 이름"),
                                fieldWithPath("data.friends[].email").description("친구 이메일"),
                                fieldWithPath("data.friends[].status").description("친구 상태 (friend, normal)"),
                                fieldWithPath("data.friends[].birthday").description("친구 생년월일")
                        )
                ));

        verify(friendFacade, times(1)).getFriends(any(UserId.class));
    }
}
