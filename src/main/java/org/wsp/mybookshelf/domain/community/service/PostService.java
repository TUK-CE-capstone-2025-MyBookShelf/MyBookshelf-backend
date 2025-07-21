package org.wsp.mybookshelf.domain.community.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wsp.mybookshelf.domain.community.dto.PostRequestDto;
import org.wsp.mybookshelf.domain.community.dto.PostResponseDto;
import org.wsp.mybookshelf.domain.community.entity.BoardType;
import org.wsp.mybookshelf.domain.community.entity.Post;
import org.wsp.mybookshelf.domain.community.repository.PostLikeRepository;
import org.wsp.mybookshelf.domain.community.repository.PostRepository;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository; // 필요 시 사용
    private final ModelMapper modelMapper = new ModelMapper(); // or 수동 매핑

    // 1. 게시글 생성
    public PostResponseDto createPost(PostRequestDto dto, User user, Long userId) {
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .isAnonymous(dto.isAnonymous())
                .boardType(dto.getBoardType())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
        return toDto(post, userId);
    }

    // 2. 게시글 목록 조회 (자유 게시판만)
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts(BoardType boardType, Long userId) {
        List<Post> posts = postRepository.findByBoardTypeOrderByCreatedAtDesc(boardType);
        return posts.stream()
                .map(post -> toDto(post, userId))
                .toList();
    }

    // 3. 게시글 상세 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return toDto(post, userId);
    }

    // 4. 게시글 수정
    public PostResponseDto updatePost(Long id, PostRequestDto dto, User user, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        validateAuthor(post, user);

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAnonymous(dto.isAnonymous());
        post.setUpdatedAt(LocalDateTime.now());

        return toDto(post, userId);
    }

    // 5. 게시글 삭제
    public void deletePost(Long id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        validateAuthor(post, user);
        postRepository.delete(post);
    }

    // 작성자 확인
    private void validateAuthor(Post post, User user) {
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("작성자만 수정/삭제할 수 있습니다.");
        }
    }

    // 응답 DTO로 변환
    private PostResponseDto toDto(Post post, Long userId) {
        String authorName = post.isAnonymous() ? "익명" : post.getAuthor().getNickName();
        long likeCount = postLikeRepository.countByPost(post);
        Boolean likedByUser = null;
        if (userId != null) {
            likedByUser = postLikeRepository.existsByPostAndUser(post, userRepository.findById(userId).orElse(null));
        }

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(authorName)
                .boardType(post.getBoardType())
                .isAnonymous(post.isAnonymous())
                .createdAt(post.getCreatedAt())
                .likeCount(likeCount)
                .likedByUser(likedByUser)
                .build();
    }

}

