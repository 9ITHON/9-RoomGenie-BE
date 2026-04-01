package team9.demo.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGPTRequest {


    private String model;
    private List<Message> messages;

    @JsonProperty("max_tokens")
    private int maxTokens;

    /** 편의 팩토리 */
    public static ChatGPTRequest of(String model, String requestText, String imageDataUrl, int maxTokens) {
        return ChatGPTRequest.builder()
                .model(model)
                .messages(List.of(
                        Message.builder()
                                .role("user")
                                .content(List.of(
                                        Message.ContentText.of(requestText),
                                        Message.ContentImage.of(imageDataUrl)
                                ))
                                .build()
                ))
                .maxTokens(maxTokens)
                .build();
    }

    // ---------- nested types ----------
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private List<Object> content; // ContentText / ContentImage

        @Getter @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ContentText {
            @Builder.Default
            private String type = "text";
            private String text;

            public static ContentText of(String text) {
                return ContentText.builder().text(text).build();
            }
        }

        @Getter @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ContentImage {
            @Builder.Default
            private String type = "image_url";

            @JsonProperty("image_url")
            private Url imageUrl;

            public static ContentImage of(String url) {
                return ContentImage.builder().imageUrl(new Url(url)).build();
            }

            @Getter @Setter
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            public static class Url {
                private String url;
            }
        }
    }



}
