package team9.demo.external;

import team9.demo.model.media.FileData;
import team9.demo.model.media.Media;


public interface ExternalFileClient {

    void uploadFile(FileData file, Media media);

    void removeFile(Media media);
}