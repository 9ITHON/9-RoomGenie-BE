package team9.demo.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.demo.dto.request.user.SearchRequest;
import team9.demo.dto.response.user.SearchResponse;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.response.HttpResponse;
import team9.demo.response.SuccessOnlyResponse;
import team9.demo.service.user.UserService;
import team9.demo.util.helper.ResponseHelper;
import team9.demo.util.security.CurrentUser;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @GetMapping("/search")
    public ResponseEntity<HttpResponse<SearchResponse>> searchUser(
            @CurrentUser UserId userId,
            @RequestParam("name") String name
    ) {
        UserId targetId = userService.searchUser(name);
        return ResponseHelper.success(SearchResponse.of(targetId.getId(), name));
    }






}
