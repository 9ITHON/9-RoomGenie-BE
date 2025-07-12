package team9.demo.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.demo.dto.request.mission.TodayMissionAcceptRequest;
import team9.demo.dto.request.mission.TodayMissionRequest;
import team9.demo.dto.response.mission.TodayMissionResponse;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.service.user.UserService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


}
