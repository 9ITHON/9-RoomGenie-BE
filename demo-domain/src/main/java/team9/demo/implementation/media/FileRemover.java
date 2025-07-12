package team9.demo.implementation.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.external.ExternalFileClient;
import team9.demo.model.media.Media;


@Component
@RequiredArgsConstructor
public class FileRemover {

    private final ExternalFileClient externalFileClient;

    public void removeFile(Media media) {
        externalFileClient.removeFile(media);
    }
}