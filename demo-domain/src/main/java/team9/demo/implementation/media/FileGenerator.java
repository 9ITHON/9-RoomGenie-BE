package team9.demo.implementation.media;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.FileData;

import team9.demo.model.media.Media;
import team9.demo.model.user.UserId;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileGenerator {

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Media generateMedia(FileData file, UserId userId, FileCategory category) {
        return Media.upload(baseUrl, bucket, category, userId, file.getName(), file.getContentType());
    }

    public List<Map.Entry<FileData, Media>> generateMedias(List<FileData> files, UserId userId, FileCategory category) {
        return files.stream()
                .map(file -> new AbstractMap.SimpleEntry<>(
                        file,
                        Media.upload(baseUrl, bucket, category, userId, file.getName(), file.getContentType())
                ))
                .collect(Collectors.toList());
    }
}