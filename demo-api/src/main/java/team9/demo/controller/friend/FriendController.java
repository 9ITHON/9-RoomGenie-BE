package team9.demo.controller.friend;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.demo.dto.request.friend.FriendRequest;
import team9.demo.dto.response.friend.FriendListResponse;
import team9.demo.dto.response.friend.FriendResponse;
import team9.demo.facade.friend.FriendFacade;
import team9.demo.model.friend.Friend;
import team9.demo.model.user.User;
import team9.demo.model.user.UserId;
import team9.demo.response.HttpResponse;
import team9.demo.response.SuccessOnlyResponse;
import team9.demo.service.friend.FriendShipService;
import team9.demo.util.helper.ResponseHelper;
import team9.demo.util.security.CurrentUser;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendShipService friendShipService;
    private final FriendFacade friendFacade;

    // 일단은 단방향 친구추가로 기획
    @PostMapping("")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> createFriend(
            @CurrentUser UserId userId,
            @RequestBody FriendRequest.Create request
    ){
        String targetId = request.targetId();
        friendFacade.createFriend(userId, UserId.of(targetId));
        return ResponseHelper.successOnly();
    }

    @DeleteMapping("")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> deleteFriend(
            @CurrentUser UserId userId,
            @RequestBody FriendRequest.Delete request
    ){
        String targetId = request.targetId();
        friendFacade.deleteFriend(userId, UserId.of(targetId));
        return ResponseHelper.successOnly();

    }

    @GetMapping("/list")
    public ResponseEntity<HttpResponse<FriendListResponse>> getFriends(
            @CurrentUser UserId userId
    ){
        List<Friend> friends = friendFacade.getFriends(userId);
        return ResponseHelper.success(FriendListResponse.of(friends));
    }




}
