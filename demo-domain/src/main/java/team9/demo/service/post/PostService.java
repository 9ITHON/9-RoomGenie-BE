package team9.demo.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.implementation.media.FileHandler;
import team9.demo.implementation.post.PostAppender;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.FileData;
import team9.demo.model.media.Media;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final FileHandler fileHandler;
    private final PostAppender postAppender;



    @Transactional
    public PostId createPost(UserId userId, List<FileData> files, String title, String content) {
        List<Media> medias = fileHandler.handleNewFiles(userId, files, FileCategory.POST);
        return postAppender.append(medias, userId, title, content);
    }



}

