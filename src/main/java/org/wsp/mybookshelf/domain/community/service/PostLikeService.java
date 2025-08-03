package org.wsp.mybookshelf.domain.community.service;

import org.wsp.mybookshelf.domain.community.entity.Post;
import org.wsp.mybookshelf.domain.community.entity.PostLike;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.community.repository.PostLikeRepository;
import org.wsp.mybookshelf.domain.community.repository.PostRepository;
import org.wsp.mybookshelf.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            postLikeRepository.deleteByPostAndUser(post, user);
            return false; // 좋아요 취소
        } else {
            postLikeRepository.save(new PostLike(post, user));
            return true; // 좋아요 추가
        }
    }

    public long countLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return postLikeRepository.countByPost(post);
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        return postLikeRepository.existsByPostAndUser(post, user);
    }
}