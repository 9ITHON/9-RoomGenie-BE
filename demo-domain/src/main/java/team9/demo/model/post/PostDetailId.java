package team9.demo.model.post;

import lombok.Value;

@Value(staticConstructor = "of")
public class PostDetailId {
    String id;
}
