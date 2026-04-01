package team9.demo.model.user;

import lombok.Value;

import java.util.Objects;

@Value(staticConstructor = "of")
public class UserId {
    String id;
}