package team9.demo.model.post;

public final class PostId {

    private final String id;

    private PostId(String id) {
        this.id = id;
    }

    public static PostId of(String id) {
        return new PostId(id);
    }

    public String getId() {
        return id;
    }

    // equals, hashCode, toString 오버라이드 권장
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostId)) return false;
        PostId postId = (PostId) o;
        return id.equals(postId.id);  // 여기서 postId.id로 변경
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "PostId{" + "id='" + id + '\'' + '}';
    }
}
