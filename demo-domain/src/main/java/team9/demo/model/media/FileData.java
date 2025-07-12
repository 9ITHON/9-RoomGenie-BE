package team9.demo.model.media;

import lombok.Getter;

import java.io.InputStream;

@Getter
public class FileData {
    private final InputStream inputStream;
    private final MediaType contentType;
    private final String name;
    private final long size;

    // ✅ 추가된 필드 (기존 코드 영향 없음)
    private final byte[] content;
    private final int width;
    private final int height;

    // ✅ 기존 생성자 (유지)
    private FileData(InputStream inputStream, MediaType contentType, String name, long size) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.name = name;
        this.size = size;

        // 기본값 설정
        this.content = null;
        this.width = 0;
        this.height = 0;
    }

    // ✅ 기존 of 메서드 (유지)
    public static FileData of(InputStream inputStream, MediaType contentType, String name, long size) {
        return new FileData(inputStream, contentType, name, size);
    }

    // ✅ 새 기능용 생성자
    private FileData(InputStream inputStream, MediaType contentType, String name, long size,
                     byte[] content, int width, int height) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.name = name;
        this.size = size;
        this.content = content;
        this.width = width;
        this.height = height;
    }

    // ✅ 새 기능용 of 메서드
    public static FileData of(InputStream inputStream, MediaType contentType, String name, long size,
                              byte[] content, int width, int height) {
        return new FileData(inputStream, contentType, name, size, content, width, height);
    }
}
