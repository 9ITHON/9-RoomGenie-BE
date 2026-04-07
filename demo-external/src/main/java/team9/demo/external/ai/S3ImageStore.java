package team9.demo.external.ai;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.model.user.UserId;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

/**
 * AI 파이프라인 전용 S3 헬퍼.
 * - GPT Vision 입력용: 비공개 버킷 객체를 다운로드 후 Base64 인코딩
 * - LAMA 결과물 업로드: inpainting 산출물 PNG 저장 후 public URL 반환
 *
 * 일반 사용자 파일 업로드는 ExternalFileClientImpl이 담당하며,
 * 본 클래스는 AI 파이프라인 내부 전용으로만 사용한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageStore {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /** S3 오브젝트를 다운로드해 byte 배열로 반환한다. */
    public byte[] download(String imageUrl) throws IOException {
        String key = extractKey(imageUrl);
        S3Object object = amazonS3.getObject(bucket, key);
        try (InputStream inputStream = object.getObjectContent()) {
            return inputStream.readAllBytes();
        }
    }

    /** GPT Vision data URL 입력용 Base64 문자열로 인코딩한다. */
    public String encodeBase64(String imageUrl) throws IOException {
        return Base64.getEncoder().encodeToString(download(imageUrl));
    }

    /** AI 산출물(PNG)을 사용자 폴더에 업로드하고 public URL을 반환한다. */
    public String uploadCleanedImage(byte[] image, UserId userId) {
        String key = "AI/cleaned/" + userId.getId() + "/" + UUID.randomUUID() + ".png";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.length);
        metadata.setContentType("image/png");

        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            amazonS3.putObject(bucket, key, inputStream, metadata);
        } catch (IOException e) {
            log.error("S3 업로드 실패: {}", e.getMessage(), e);
            throw new AiException(ErrorCode.AI_S3_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, key).toString();
    }

    /** S3 URL(path-style / virtual-hosted style)에서 object key를 추출한다. */
    private String extractKey(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        String host = url.getHost();
        String path = url.getPath();

        if (path.startsWith("/" + bucket + "/")) {
            return path.substring(("/" + bucket + "/").length());
        }
        if (host.startsWith(bucket + ".")) {
            return path.startsWith("/") ? path.substring(1) : path;
        }
        throw new AiException(ErrorCode.AI_IMAGE_READ_FAILED);
    }
}
