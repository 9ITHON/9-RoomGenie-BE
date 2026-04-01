package team9.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import team9.demo.RestDocsTest;
import team9.demo.TestDataFactory;
import team9.demo.TestUserArgumentResolver;
import team9.demo.controller.post.PostController;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.facade.post.PostFacade;
import team9.demo.model.comment.Comment;
import team9.demo.model.media.Media;
import team9.demo.model.post.*;
import team9.demo.model.user.UserId;
import team9.demo.service.post.PostService;
import team9.demo.util.handler.GlobalExceptionHandler;
import team9.demo.util.security.UserArgumentResolver;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team9.demo.RestDocsUtils.*;

@ActiveProfiles("test")
public class PostControllerTest extends RestDocsTest {

    private PostService postService;
    private PostFacade postFacade;
    private MockMvc standaloneMockMvc;

    @BeforeEach
    void setUpController(RestDocumentationContextProvider restDocumentation) {
        postService = mock(PostService.class);
        postFacade = mock(PostFacade.class);
        PostController controller = new PostController(postService, postFacade);

        UserId userId = UserId.of("testUserId");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null)
        );

        ObjectMapper mapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        standaloneMockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new UserArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("게시글 생성 - 성공")
    void createPost() throws Exception {
        MockMultipartFile file = TestDataFactory.createTestImageFile("files", "test.png");

        PostId postId = PostId.of("newPostId");
        when(postService.createPost(any(), any(), any(), any(), any(), any())).thenReturn(postId);

        standaloneMockMvc.perform(multipart("/api/post")
                        .file(file)
                        .param("title", "오늘의 청소 기록")
                        .param("content", "거실 청소 완료!")
                        .param("friendIds", "friend1", "friend2")
                        .header("Authorization", "Bearer testToken")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.postId").value("newPostId"))
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestParts(
                                partWithName("files").description("업로드할 이미지 파일 (복수 가능)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.postId").description("생성된 게시글 ID")
                        )
                ));

        verify(postService, times(1)).createPost(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 성공")
    void getPosts() throws Exception {
        PostInfo postInfo = PostInfo.of(
                PostId.of("post1"), UserId.of("user1"),
                LocalDateTime.of(2026, 4, 1, 10, 0, 0),
                "청소 기록", "거실 청소!", 3L
        );
        Comment comment = Comment.of(
                UserId.of("commenter1"), PostId.of("post1"),
                "잘했어요!", LocalDateTime.of(2026, 4, 1, 11, 0, 0)
        );
        Post post = Post.of(postInfo, List.of(comment), Collections.emptyList());

        when(postFacade.getPosts(any(UserId.class), any())).thenReturn(List.of(post));

        standaloneMockMvc.perform(get("/api/post")
                        .param("friendIds", "friend1")
                        .header("Authorization", "Bearer testToken")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.posts[0].postId").value("post1"))
                .andExpect(jsonPath("$.data.posts[0].title").value("청소 기록"))
                .andDo(document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        queryParameters(
                                parameterWithName("friendIds").description("조회할 친구 ID 목록")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.posts[]").description("게시글 목록"),
                                fieldWithPath("data.posts[].postId").description("게시글 ID"),
                                fieldWithPath("data.posts[].uploadTime").description("업로드 시간 (yyyy-MM-dd HH:mm:ss)"),
                                fieldWithPath("data.posts[].title").description("게시글 제목"),
                                fieldWithPath("data.posts[].content").description("게시글 내용"),
                                fieldWithPath("data.posts[].postDetails[]").description("첨부 파일 목록"),
                                fieldWithPath("data.posts[].comments[]").description("댓글 목록"),
                                fieldWithPath("data.posts[].comments[].userId").description("댓글 작성자 ID"),
                                fieldWithPath("data.posts[].comments[].postId").description("댓글 대상 게시글 ID"),
                                fieldWithPath("data.posts[].comments[].content").description("댓글 내용"),
                                fieldWithPath("data.posts[].comments[].uploadTime").description("댓글 작성 시간")
                        )
                ));

        verify(postFacade, times(1)).getPosts(any(UserId.class), any());
    }
}
