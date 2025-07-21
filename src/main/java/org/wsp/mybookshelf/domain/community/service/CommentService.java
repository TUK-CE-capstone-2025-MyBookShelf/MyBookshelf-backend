package org.wsp.mybookshelf.domain.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wsp.mybookshelf.domain.community.dto.CommentRequestDto;
import org.wsp.mybookshelf.domain.community.dto.CommentResponseDto;
import org.wsp.mybookshelf.domain.community.entity.Comment;
import org.wsp.mybookshelf.domain.community.entity.Post;
import org.wsp.mybookshelf.domain.community.repository.CommentRepository;
import org.wsp.mybookshelf.domain.community.repository.PostRepository;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto dto, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .author(user)
                .content(dto.getContent())
                .isAnonymous(dto.isAnonymous())
                .build();

        commentRepository.save(comment);
        return toDto(comment);
    }

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        return commentRepository.findByPostOrderByCreatedAtAsc(post).stream()
                .map(this::toDto)
                .toList();
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto dto, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.update(dto.getContent(), dto.isAnonymous());
        return toDto(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    // DTO 변환
    private CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.isAnonymous() ? "익명" : comment.getAuthor().getNickName())
                .content(comment.getContent())
                .isAnonymous(comment.isAnonymous())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}