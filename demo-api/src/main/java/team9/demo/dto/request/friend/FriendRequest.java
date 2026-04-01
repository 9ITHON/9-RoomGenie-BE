package team9.demo.dto.request.friend;

public final class FriendRequest {

    public record Create(String targetId) { }

    public record Delete(String targetId) { }
}
