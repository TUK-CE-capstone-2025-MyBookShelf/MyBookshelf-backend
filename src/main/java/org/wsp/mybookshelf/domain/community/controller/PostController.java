package org.wsp.mybookshelf.domain.community.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.wsp.mybookshelf.domain.community.dto.PostRequestDto;
import org.wsp.mybookshelf.domain.community.dto.PostResponseDto;
import org.wsp.mybookshelf.domain.community.entity.BoardType;
import org.wsp.mybookshelf.domain.community.service.PostLikeService;
import org.wsp.mybookshelf.domain.community.service.PostService;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.service.UserService;
import org.wsp.mybookshelf.global.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final UserService userService;

    // 1. 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @RequestBody PostRequestDto requestDto,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", "로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        PostResponseDto response = postService.createPost(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }

    // 2. 게시글 목록 조회 (기본: 자유게시판)
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponseDto>>> getPosts(
            @RequestParam(defaultValue = "FREE") BoardType boardType) {
        List<PostResponseDto> posts = postService.getPosts(boardType);
        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    // 3. 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(@PathVariable Long id) {
        PostResponseDto post = postService.getPost(id);
        return ResponseEntity.ok(ApiResponse.onSuccess(post));
    }

    // 4. 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequestDto requestDto,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", "로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        PostResponseDto updated = postService.updatePost(id, requestDto, user);
        return ResponseEntity.ok(ApiResponse.onSuccess(updated));
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable Long id,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", "로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        postService.deletePost(id, user);
        return ResponseEntity.ok(ApiResponse.onSuccess("게시글이 삭제되었습니다."));
    }

    // 좋아요 토글 (누르면 등록/취소)
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        boolean liked = postLikeService.toggleLike(postId, userId);
        return ResponseEntity.ok(liked ? "좋아요 등록됨" : "좋아요 취소됨");
    }

    // 해당 게시글의 좋아요 수 반환
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        long count = postLikeService.countLikes(postId);
        return ResponseEntity.ok(count);
    }

    // 내가 해당 게시글에 좋아요 눌렀는지
    @GetMapping("/{postId}/liked")
    public ResponseEntity<Boolean> isLikedByUser(@PathVariable Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(false);
        }
        boolean liked = postLikeService.isLikedByUser(postId, userId);
        return ResponseEntity.ok(liked);
    }
}
