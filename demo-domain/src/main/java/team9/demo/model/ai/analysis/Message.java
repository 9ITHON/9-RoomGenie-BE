package team9.demo.model.ai.analysis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;    // "user" | "assistant"
    private String content; // 사용자 입력 또는 이미지 설명 등
}