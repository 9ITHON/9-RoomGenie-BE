package team9.demo.implementation.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.FileData;
import team9.demo.model.media.Media;
import team9.demo.model.media.MediaType;
import team9.demo.model.user.UserId;
import team9.demo.util.AsyncJobExecutor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileAppender fileAppender;
    private final FileRemover fileRemover;
    private final FileGenerator fileGenerator;
    private final FileValidator fileValidator;
    private final AsyncJobExecutor asyncJobExecutor;

    public List<Media> handleNewFiles(UserId userId, List<FileData> files, FileCategory category) {
        fileValidator.validateFilesNameCorrect(files);
        List<Map.Entry<FileData, Media>> mediaWithFiles = fileGenerator.generateMedias(files, userId, category);
        try {
            asyncJobExecutor.executeAsyncJobs(mediaWithFiles, pair ->
                    fileAppender.appendFile(pair.getKey(), pair.getValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mediaWithFiles.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Media handleNewFile(UserId userId, FileData file, FileCategory category) {
        fileValidator.validateFileNameCorrect(file);
        Media media = fileGenerator.generateMedia(file, userId, category);
        try {
            asyncJobExecutor.executeAsyncJob(media, item -> fileAppender.appendFile(file, media));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return media;
    }

    public void handleOldFile(Media media) {
        if (!MediaType.IMAGE_BASIC.equals(media.getType())) {
            try {
                asyncJobExecutor.executeAsyncJob(media, fileRemover::removeFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleOldFiles(List<Media> medias) {
        try {
            asyncJobExecutor.executeAsyncJobs(medias, fileRemover::removeFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}