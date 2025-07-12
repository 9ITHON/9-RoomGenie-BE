package team9.demo.implementation.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.external.ExternalFileClient;
import team9.demo.model.media.FileData;

import team9.demo.model.media.Media;

@Component
@RequiredArgsConstructor
public class FileAppender {

    private final ExternalFileClient externalFileClient;

    public void appendFile(FileData file, Media media) {
        externalFileClient.uploadFile(file, media);
    }
}