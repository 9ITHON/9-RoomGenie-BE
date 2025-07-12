package team9.demo.controller.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.demo.dto.request.mission.TodayMissionAcceptRequest;
import team9.demo.dto.request.mission.TodayMissionRequest;
import team9.demo.dto.response.mission.TodayMissionResponse;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.response.HttpResponse;
import team9.demo.response.SuccessOnlyResponse;
import team9.demo.service.mission.MissionService;
import team9.demo.util.helper.ResponseHelper;
import team9.demo.util.security.CurrentUser;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/today-mission")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/recommend")
    public ResponseEntity<Map<String, String>> recommendTodayMission(@CurrentUser UserId userId) {
        String mission = missionService.recommendOneMission(userId);
        return ResponseEntity.ok(Map.of("mission", mission));
    }

    @PostMapping("")
    public ResponseEntity<Void>  makeCustomTodayMission(
            @CurrentUser UserId userId,
            @RequestBody TodayMissionRequest request
    ) {
        missionService.makeCustomTodayMission(userId, request.getMission());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptRecommendedTodayMission(
            @CurrentUser UserId userId,
            @RequestBody TodayMissionAcceptRequest request
    ) {
        missionService.acceptRecommendedTodayMission(userId, request.getMissionId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<TodayMissionResponse>> getTodayMissions(@CurrentUser UserId userId) {
        List<TodayMissionInfo> missionInfos = missionService.getTodayMissions(userId);
        List<TodayMissionResponse> responses = missionInfos.stream()
                .map(TodayMissionResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{todayMissionId}/")
    public ResponseEntity<TodayMissionResponse> getTodayMission(
            @CurrentUser UserId userId,
            @PathVariable String todayMissionId
    ) {
        TodayMissionInfo info = missionService.getTodayMission(userId, todayMissionId);
        return ResponseEntity.ok(TodayMissionResponse.from(info));
    }

}
