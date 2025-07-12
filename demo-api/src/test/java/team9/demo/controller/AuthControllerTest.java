//package team9.demo.controller;
//
//import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//
//import static org.hamcrest.Matchers.equalTo;
//import static org.mockito.Mockito.*;
//
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static team9.demo.RestDocsUtils.*;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ActiveProfiles;
//import team9.demo.RestDocsTest;
//import team9.demo.TestDataFactory;
//import team9.demo.controller.auth.AuthController;
//import team9.demo.dto.request.auth.LoginRequest;
//import team9.demo.dto.request.auth.SignUpRequest;
//import team9.demo.dto.request.auth.VerificationRequest;
//import team9.demo.error.AuthorizationException;
//import team9.demo.error.ConflictException;
//import team9.demo.error.ErrorCode;
//import team9.demo.error.NotFoundException;
//import team9.demo.facade.AccountFacade;
//import team9.demo.model.auth.CredentialTarget;
//import team9.demo.model.auth.JwtToken;
//import team9.demo.model.user.UserId;
//import team9.demo.service.auth.AuthService;
//import team9.demo.util.handler.GlobalExceptionHandler;
//import team9.demo.util.security.JwtTokenUtil;
//import team9.demo.util.security.UserArgumentResolver;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//
//@ActiveProfiles("test")
//public class AuthControllerTest extends RestDocsTest {
//
//    private AuthController authController;
//    private AuthService authService;
//    private AccountFacade accountFacade;
//    private GlobalExceptionHandler exceptionHandler;
//    private JwtTokenUtil jwtTokenUtil;
//    private UserArgumentResolver userArgumentResolver;
//
//    @BeforeEach
//    void setUp() {
//        authService = mock(AuthService.class);
//        accountFacade = mock(AccountFacade.class);
//        exceptionHandler = new GlobalExceptionHandler();
//        jwtTokenUtil = mock(JwtTokenUtil.class);
//        userArgumentResolver = new UserArgumentResolver();
//
//        authController = new AuthController(authService, accountFacade, jwtTokenUtil);
//        mockMvc = mockController(authController, exceptionHandler, userArgumentResolver);
//
//        UserId userId = UserId.of("testUserId");
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(userId, null);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
//
//    @Test
//    @DisplayName("생성을 위한 휴대폰 인증번호 전송")
//    void sendPhoneVerification() {
//        VerificationRequest.Phone requestBody = new VerificationRequest.Phone("82", "01012345678");
//
//        doNothing().when(accountFacade).registerCredential(any(), eq(CredentialTarget.SIGN_UP));
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/create/send")
//                .then();
//
//        // ✅ static 메서드 호출로 assert
//        assertCommonSuccessResponse(response)
//                .apply(document("{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        requestFields(
//                                fieldWithPath("countryCode").description("국가 코드"),
//                                fieldWithPath("phoneNumber").description("휴대폰 번호")
//                        ),
//                        responseSuccessFields()
//                ));
//
//        verify(accountFacade, times(1)).registerCredential(any(), eq(CredentialTarget.SIGN_UP));
//    }
//
//    @Test
//    @DisplayName("생성을 위한 휴대폰 인증번호 전송 실패 - 이미 생성된 계정")
//    void sendPhoneVerificationAlreadyCreated() {
//        VerificationRequest.Phone requestBody = new VerificationRequest.Phone("82", "01012345678");
//
//        doThrow(new ConflictException(ErrorCode.USER_ALREADY_CREATED))
//                .when(accountFacade)
//                .registerCredential(any(), eq(CredentialTarget.SIGN_UP));
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/create/send")
//                .then();
//
//        // ✅ static 메서드 호출로 assert
//        assertErrorResponse(response, HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_CREATED)
//                .apply(document("{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseErrorFields(
//                                HttpStatus.CONFLICT,
//                                ErrorCode.USER_ALREADY_CREATED,
//                                "계정이 이미 생성되었음으로 로그인으로 유도 해야함"
//                        )
//                ));
//    }
//
//
//    @Test
//    @DisplayName("휴대폰 인증번호 확인")
//    void signUp() {
//        JwtToken jwtToken = TestDataFactory.createJwtToken();
//        UserId userId = TestDataFactory.createUserId();
//
//        SignUpRequest.Phone requestBody = new SignUpRequest.Phone(
//                "01012345678",
//                "82",
//                "123",
//                "testDeviceId",
//                "ios",
//                "testToken",
//                "testName"
//        );
//
//        when(accountFacade.createUser(any(), any(), any(), any(), any())).thenReturn(userId);
//        when(jwtTokenUtil.createJwtToken(userId)).thenReturn(jwtToken);
//        doNothing().when(authService).createLoginInfo(userId, jwtToken.getRefreshToken());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/create/verify")
//                .then();
//
//        response.statusCode(200)
//                .body("status", equalTo(200))
//                .body("data.accessToken", equalTo(jwtToken.getAccessToken()))
//                .body("data.refreshToken", equalTo(jwtToken.getRefreshToken().getToken()))
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        requestFields(
//                                fieldWithPath("phoneNumber").description("휴대폰 번호"),
//                                fieldWithPath("countryCode").description("국가 코드"),
//                                fieldWithPath("verificationCode").description("인증번호"),
//                                fieldWithPath("deviceId").description("디바이스 아이디(디바이스 식별을 위한 정보)"),
//                                fieldWithPath("provider").description("플랫폼(ios, android)"),
//                                fieldWithPath("appToken").description("앱 토큰(푸시 토큰)"),
//                                fieldWithPath("userName").description("사용자 이름")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").description("상태 코드"),
//                                fieldWithPath("data.accessToken").description("액세스 토큰"),
//                                fieldWithPath("data.refreshToken").description("리프레시 토큰")
//                        )
//                ));
//
//        verify(accountFacade, times(1)).createUser(any(), any(), any(), any(), any());
//    }
//
//    @Test
//    @DisplayName("휴대폰 인증번호 확인 실패 - 잘못된 인증번호")
//    void signUpWrongVerificationCode() {
//        SignUpRequest.Phone requestBody = new SignUpRequest.Phone(
//                "01012345678",
//                "82",
//                "123",
//                "testDeviceId",
//                "ios",
//                "testToken",
//                "testName"
//        );
//
//        doThrow(new AuthorizationException(ErrorCode.WRONG_VERIFICATION_CODE))
//                .when(accountFacade)
//                .createUser(any(), any(), any(), any(), any());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/create/verify")
//                .then();
//
//        assertErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_VERIFICATION_CODE)
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseErrorFields(
//                                HttpStatus.UNAUTHORIZED,
//                                ErrorCode.WRONG_VERIFICATION_CODE,
//                                "휴대폰 인증번호가 잘못되었음 - 재인증 요청으로 유도해야함"
//                        )
//                ));
//
//        verify(accountFacade, times(1)).createUser(any(), any(), any(), any(), any());
//    }
//
//
//    @Test
//    @DisplayName("휴대폰 인증번호 확인 실패 - 잘못된 인증번호")
//    void signUpExpiredVerificationCode() {
//        SignUpRequest.Phone requestBody = new SignUpRequest.Phone(
//                "01012345678",
//                "82",
//                "123",
//                "testDeviceId",
//                "ios",
//                "testToken",
//                "testName"
//        );
//
//        doThrow(new AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE))
//                .when(accountFacade)
//                .createUser(any(), any(), any(), any(), any());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/create/verify")
//                .then();
//
//        assertErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_VERIFICATION_CODE)
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseErrorFields(
//                                HttpStatus.UNAUTHORIZED,
//                                ErrorCode.EXPIRED_VERIFICATION_CODE,
//                                "휴대폰 인증번호가 만료됨 - 재인증 요청으로 유도해야함"
//                        )
//                ));
//
//        verify(accountFacade, times(1)).createUser(any(), any(), any(), any(), any());
//    }
//
//
//
//    @Test
//    @DisplayName("휴대폰 인증번호 확인 실패 - 이미 생성된 계정")
//    void signUpAlreadyCreated() {
//        SignUpRequest.Phone requestBody = new SignUpRequest.Phone(
//                "01012345678",
//                "82",
//                "123",
//                "testDeviceId",
//                "ios",
//                "testToken",
//                "testName"
//        );
//
//        doThrow(new ConflictException(ErrorCode.USER_ALREADY_CREATED))
//                .when(accountFacade)
//                .createUser(any(), any(), any(), any(), any());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/create/verify")
//                .then();
//
//        assertErrorResponse(response, HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_CREATED)
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseErrorFields(
//                                HttpStatus.CONFLICT,
//                                ErrorCode.USER_ALREADY_CREATED,
//                                "계정이 이미 생성되었으므로 로그인으로 유도 해야함. 다만, 전화번호 인증 요청시 이미 검증이 되었는데 발생한 잘못된 접근 임."
//                        )
//                ));
//
//        verify(accountFacade, times(1)).createUser(any(), any(), any(), any(), any());
//    }
//
//
//    @Test
//    @DisplayName("비밀번호 생성")
//    void createPassword() {
//        String userId = "testUserId";
//
//        SignUpRequest.Password requestBody = new SignUpRequest.Password("testPassword");
//
//        doNothing().when(accountFacade).createPassword(any(), any());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .header("Authorization", "Bearer access-token")
//                .attribute("userId", userId)
//                .post("/api/auth/create/password")
//                .then();
//
//        assertCommonSuccessCreateResponse(response)
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        requestAccessTokenFields(),
//                        requestFields(
//                                fieldWithPath("password").description("생성할 비밀번호")
//                        ),
//                        responseSuccessFields()
//                ));
//
//        verify(accountFacade, times(1)).createPassword(any(), any());
//    }
//
//
//    @Test
//    @DisplayName("로그인")
//    void login() {
//        JwtToken jwtToken = TestDataFactory.createJwtToken();
//        UserId userId = TestDataFactory.createUserId();
//
//        LoginRequest requestBody = new LoginRequest(
//                "testPassword",
//                "82",
//                "01012345678",
//                "testDeviceId",
//                "ios",
//                "testToken"
//        );
//
//        when(accountFacade.login(any(), any(), any(), any())).thenReturn(userId);
//        when(jwtTokenUtil.createJwtToken(userId)).thenReturn(jwtToken);
//        doNothing().when(authService).createLoginInfo(userId, jwtToken.getRefreshToken());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/login")
//                .then();
//
//        response.statusCode(200)
//                .body("status", equalTo(200))
//                .body("data.accessToken", equalTo(jwtToken.getAccessToken()))
//                .body("data.refreshToken", equalTo(jwtToken.getRefreshToken().getToken()))
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        requestFields(
//                                fieldWithPath("phoneNumber").description("휴대폰 번호"),
//                                fieldWithPath("countryCode").description("국가 코드"),
//                                fieldWithPath("password").description("비밀번호"),
//                                fieldWithPath("deviceId").description("디바이스 아이디(디바이스 식별을 위한 정보)"),
//                                fieldWithPath("provider").description("플랫폼(ios, android)"),
//                                fieldWithPath("appToken").description("앱 토큰(푸시 토큰)")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").description("상태 코드"),
//                                fieldWithPath("data.accessToken").description("액세스 토큰"),
//                                fieldWithPath("data.refreshToken").description("리프레시 토큰")
//                        )
//                ));
//
//        verify(accountFacade, times(1)).login(any(), any(), any(), any());
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 잘못된 비밀번호")
//    void loginWrongPassword() {
//        LoginRequest requestBody = new LoginRequest(
//                "01012345678",
//                "82",
//                "testPassword",
//                "testDeviceId",
//                "ios",
//                "testToken"
//        );
//
//        doThrow(new AuthorizationException(ErrorCode.WRONG_PASSWORD))
//                .when(accountFacade)
//                .login(any(), any(), any(), any());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/login")
//                .then();
//
//        assertErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_PASSWORD)
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseErrorFields(
//                                HttpStatus.UNAUTHORIZED,
//                                ErrorCode.WRONG_PASSWORD,
//                                "비밀번호가 잘못되었음 - 새로운 인증 번호 요청으로 유도해야함"
//                        )
//                ));
//
//        verify(accountFacade, times(1)).login(any(), any(), any(), any());
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 존재하지 않는 계정")
//    void loginNotFoundUser() {
//        LoginRequest requestBody = new LoginRequest(
//                "01012345678",
//                "82",
//                "testPassword",
//                "testDeviceId",
//                "ios",
//                "testToken"
//        );
//
//        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
//                .when(accountFacade)
//                .login(any(), any(), any(), any());
//
//        ValidatableMockMvcResponse response = given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(requestBody)
//                .post("/api/auth/login")
//                .then();
//
//        assertErrorResponse(response, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)
//                .apply(document(
//                        "{class-name}/{method-name}",
//                        requestPreprocessor(),
//                        responsePreprocessor(),
//                        responseErrorFields(
//                                HttpStatus.NOT_FOUND,
//                                ErrorCode.USER_NOT_FOUND,
//                                "계정이 존재하지 않음으로 회원가입을 유도해야함 - 로그인시 휴대폰 인증 할때 이미 존재 유무를 검증하는데 이때 오류 발생시 잘못된 접근"
//                        )
//                ));
//
//        verify(accountFacade, times(1)).login(any(), any(), any(), any());
//    }
//
//
//
//
//}
