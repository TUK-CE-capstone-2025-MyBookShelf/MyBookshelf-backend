package org.wsp.mybookshelf.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wsp.mybookshelf.domain.community.entity.Post;
import org.wsp.mybookshelf.domain.community.entity.PostLike;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    Long countByPost(Post post);

    void deleteByPostId(Long postId);
    void deleteByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);
}
