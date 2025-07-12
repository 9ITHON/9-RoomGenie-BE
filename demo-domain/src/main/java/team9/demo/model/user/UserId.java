package team9.demo.model.user;

import java.util.Objects;

public final class UserId {

    private final String id;

    private UserId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("UserId는 null이 될 수 없습니다.");
        }
        this.id = id;
    }

    public static UserId of(String id) {
        return new UserId(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId other = (UserId) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserId{id='" + id + "'}";
    }
}