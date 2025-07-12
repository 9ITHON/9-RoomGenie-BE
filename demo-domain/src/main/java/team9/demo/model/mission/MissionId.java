package team9.demo.model.mission;

import java.util.Objects;

public final class MissionId {

    private final String id;

    private MissionId(String id) {
        this.id = id;
    }

    public static team9.demo.model.mission.MissionId of(String id) {
        return new team9.demo.model.mission.MissionId(id);
    }

    public String getId() {
        return id;
    }

    // equals and hashCode to ensure value class-like behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof team9.demo.model.mission.MissionId)) return false;
        team9.demo.model.mission.MissionId missionId = (team9.demo.model.mission.MissionId) o;
        return Objects.equals(id, missionId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "id='" + id + '\'' +
                '}';
    }
}