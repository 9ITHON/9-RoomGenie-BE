package team9.demo.implementation.friend.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team9.demo.model.friend.Friend;
import team9.demo.model.friend.FriendShip;
import team9.demo.model.user.User;
import team9.demo.model.user.UserId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public final class FriendAggregator {

    public List<Friend> aggregates(List<User> users, List<FriendShip> friendShips) {

        Map<String, User> userById = users.stream()
                .collect(Collectors.toMap(u -> u.getInfo().getUserId().getId(), Function.identity()));

        // 매칭되지 않는 ID가 있는지 로그로 확인(임시)
        Set<String> userIds = userById.keySet();
        Set<String> friendIds = friendShips.stream()
                .map(fs -> fs.getFriendId().getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> missing = new LinkedHashSet<>(friendIds);
        missing.removeAll(userIds);
        if (!missing.isEmpty()) {
            log.warn("missing friendIds (no matched user rows): {}", missing);
        }

        return friendShips.stream()
                .map(fs -> {
                    String key = fs.getFriendId().getId();   // ← 여기!
                    User matched = userById.get(key);
                    return matched != null ? Friend.of(matched, fs) : null;
                })
                .filter(Objects::nonNull)
                .toList();

    }


}
