package team9.demo.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.demo.dto.request.auth.LoginRequest;
import team9.demo.dto.request.auth.SignUpRequest;
import team9.demo.dto.request.auth.VerificationRequest;
import team9.demo.dto.response.auth.TokenResponse;
import team9.demo.facade.AccountFacade;
import team9.demo.model.auth.CredentialTarget;
import team9.demo.model.auth.JwtToken;
import team9.demo.model.user.UserId;
import team9.demo.response.HttpResponse;
import team9.demo.response.SuccessCreateResponse;
import team9.demo.response.SuccessOnlyResponse;
import team9.demo.service.auth.AuthService;
import team9.demo.util.helper.ResponseHelper;
import team9.demo.util.security.CurrentUser;
import team9.demo.util.security.JwtTokenUtil;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountFacade accountFacade;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/create/send")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> sendCreatePhoneVerification(
            @RequestBody VerificationRequest.Phone request
    ) {
        accountFacade.registerCredential(request.toLocalPhoneNumber(), CredentialTarget.SIGN_UP);
        return ResponseHelper.successOnly();
    }


    @PostMapping("/create/verify")
    public ResponseEntity<HttpResponse<TokenResponse>> signUp(@RequestBody SignUpRequest.Phone request) {
        UserId userId = accountFacade.createUser(
                request.toLocalPhoneNumber(),
                request.toVerificationCode(),
                request.toAppToken(),
                request.toDevice(),
                request.toUserName()
        );
        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);
        authService.createLoginInfo(userId, jwtToken.getRefreshToken());
        return ResponseHelper.success(TokenResponse.of(jwtToken));
    }

    @PostMapping("/create/password")
    public ResponseEntity<HttpResponse<SuccessCreateResponse>> makePassword(
            @RequestBody SignUpRequest.Password request,
            @CurrentUser UserId userId
    ) {
        accountFacade.createPassword(userId, request.getPassword());
        return ResponseHelper.successCreateOnly();
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        UserId userId = accountFacade.login(
                request.toLocalPhoneNumber(),
                request.toPassword(),
                request.toDevice(),
                request.toAppToken()
        );
        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);
        authService.createLoginInfo(userId, jwtToken.getRefreshToken());
        return ResponseHelper.success(TokenResponse.of(jwtToken));
    }

//    @GetMapping("/refresh")
//    public ResponseEntity<HttpResponse<TokenResponse>> refreshJwtToken(@RequestHeader("Authorization") String refreshToken) {
//        Pair<JwtToken, UserId> pair = jwtTokenUtil.refresh(refreshToken);
//        JwtToken newToken = pair.getFirst();
//        UserId userId = pair.getSecond();
//
//        String oldToken = jwtTokenUtil.cleanedToken(refreshToken);
//        authService.updateLoginInfo(oldToken, newToken.getRefreshToken(), userId);
//        return ResponseHelper.success(TokenResponse.of(newToken));
//    }
}