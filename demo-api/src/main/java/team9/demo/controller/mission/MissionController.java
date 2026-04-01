package team9.demo.controller.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.demo.dto.request.mission.MissionCustomRequest;
import team9.demo.dto.request.mission.TodayMissionAcceptRequest;
import team9.demo.dto.response.mission.MissionRecommendResponse;
import team9.demo.dto.response.mission.TodayMissionResponse;
import team9.demo.model.mission.CleaningMission;
import team9.demo.model.mission.MissionDto;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.response.HttpResponse;
import team9.demo.response.SuccessCreateResponse;
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

    @GetMapping("/recommend") //Map<String, Object>
    public ResponseEntity<HttpResponse<MissionRecommendResponse>> recommendTodayMission(@CurrentUser UserId userId) {
        CleaningMission mission = missionService.recommendOneMission(userId);
        return ResponseHelper.success(MissionRecommendResponse.from(mission));
    }


    @PostMapping("")
    public ResponseEntity<HttpResponse<SuccessCreateResponse>>  makeCustomTodayMission(
            @CurrentUser UserId userId,
            @RequestBody MissionCustomRequest request
    ) {
        missionService.makeCustomTodayMission(userId, request.mission());
        return ResponseHelper.successCreateOnly();
    }

    @PostMapping("/accept") // 미션은 중복 등록 가능
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> acceptRecommendedTodayMission(
            @CurrentUser UserId userId,
            @RequestBody TodayMissionAcceptRequest request
    ) {
        missionService.acceptRecommendedTodayMission(userId, request.missionId());
        return ResponseHelper.successOnly();
    }

    @GetMapping("/")
    public ResponseEntity<HttpResponse<List<TodayMissionResponse>>> getTodayMissions(@CurrentUser UserId userId) {
        List<TodayMissionInfo> missionInfos = missionService.getTodayMissions(userId);
        List<TodayMissionResponse> responses = missionInfos.stream()
                .map(TodayMissionResponse::from)
                .toList();
        return ResponseHelper.success(responses);
    }

    @GetMapping("/{todayMissionId}/")
    public ResponseEntity<HttpResponse<TodayMissionResponse>> getTodayMission(
            @CurrentUser UserId userId,
            @PathVariable String todayMissionId
    ) {
        TodayMissionInfo info = missionService.getTodayMission(userId, todayMissionId);
        return ResponseHelper.success(TodayMissionResponse.from(info));
    }

}
