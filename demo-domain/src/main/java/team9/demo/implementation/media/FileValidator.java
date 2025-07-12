package team9.demo.implementation.media;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.model.media.FileData;

import java.util.List;


@Component
public class FileValidator {

    public void validateFilesNameCorrect(List<FileData> files) {
        for (FileData file : files) {
            if (file.getName() == null || file.getName().isEmpty()) {
                throw new ConflictException(ErrorCode.FILE_NAME_INCORRECT);
            }
        }
    }

    public void validateFileNameCorrect(FileData file) {
        System.out.println("파일 이름: " + file.getName());
        if (file.getName() == null || file.getName().isEmpty()) {
            throw new ConflictException(ErrorCode.FILE_NAME_INCORRECT);
        }
    }
}
