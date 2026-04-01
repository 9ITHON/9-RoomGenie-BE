package team9.demo.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team9.demo.dto.response.post.PostIdResponse;
import team9.demo.dto.response.post.PostResponse;
import team9.demo.dto.response.post.PostsResponse;
import team9.demo.facade.post.PostFacade;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.FileData;
import team9.demo.model.post.Post;
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
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    private final PostFacade postFacade;

    // 추후 해시태그 추가, 게시물별 나누는 것도
    @PostMapping("")
    public ResponseEntity<HttpResponse<PostIdResponse>> createPost(
            @CurrentUser UserId userId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("friendIds") List<String> friendIds
    ) throws IOException {
        List<FileData> fileDataList = FileHelper.convertMultipartFileToFileDataList(files);
        PostId postId = postService.createPost(
                userId,
                fileDataList,
                friendIds.stream().map(UserId::of).toList(),
                title,
                content,
                FileCategory.POST

        );
        return ResponseHelper.successCreate(PostIdResponse.of(postId));
    }

    @GetMapping("")
    public ResponseEntity<HttpResponse<PostsResponse>> getPosts(
            @CurrentUser UserId userId,
            @RequestParam("friendIds") List<String> friendIds
    ) throws IOException {
        List<Post> postList = postFacade.getPosts(userId, friendIds.stream().map(UserId::of).toList());
        return ResponseHelper.success(PostsResponse.of(postList));
    }



}
