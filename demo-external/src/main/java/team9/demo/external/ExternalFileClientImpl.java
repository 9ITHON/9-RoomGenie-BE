package team9.demo.external;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.model.media.FileData;
import team9.demo.model.media.Media;


@Component
@RequiredArgsConstructor
public class ExternalFileClientImpl implements ExternalFileClient {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public void uploadFile(FileData file, Media media) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(media.getType().value());

            amazonS3.putObject(bucket, media.getPath(), file.getInputStream(), metadata);
        } catch (Exception e) {
            throw new ConflictException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void removeFile(Media media) {
        try {
            amazonS3.deleteObject(bucket, media.getPath());
        } catch (Exception e) {
            throw new ConflictException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    public String getPublicUrl(Media media) {
        return amazonS3.getUrl(bucket, media.getPath()).toString();
    }
}
