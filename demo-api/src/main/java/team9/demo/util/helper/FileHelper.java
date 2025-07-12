package team9.demo.util.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.model.media.FileData;
import team9.demo.model.media.MediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class FileHelper {

    public static FileData convertMultipartFileToFileData(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new ConflictException(ErrorCode.FILE_NAME_COULD_NOT_EMPTY);
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE);
        }

        MediaType mediaType = MediaType.fromType(contentType);
        if (mediaType == null) {
            throw new ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE);
        }

        // ✅ 이미지 byte[]로 읽기
        byte[] content = file.getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);

        // ✅ width, height 추출
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
        if (image == null) {
            throw new ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE);
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // ✅ 확장된 FileData 생성
        return FileData.of(inputStream, mediaType, originalFilename, file.getSize(), content, width, height);
    }

    public static List<FileData> convertMultipartFileToFileDataList(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return convertMultipartFileToFileData(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}