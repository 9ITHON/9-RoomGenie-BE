package team9.demo.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team9.demo.dto.response.post.PostIdResponse;
import team9.demo.model.media.FileData;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;
import team9.demo.response.HttpResponse;
import team9.demo.service.post.PostService;
import team9.demo.util.helper.FileHelper;
import team9.demo.util.helper.ResponseHelper;
import team9.demo.util.security.CurrentUser;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<HttpResponse<PostIdResponse>> createPost(
            @CurrentUser UserId userId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("title") String title,
            @RequestParam("content") String content
    ) throws IOException {
        List<FileData> fileDataList = FileHelper.convertMultipartFileToFileDataList(files);
        PostId postId = postService.createPost(userId, fileDataList, title, content);
        return ResponseHelper.successCreate(PostIdResponse.of(postId));
    }

}
