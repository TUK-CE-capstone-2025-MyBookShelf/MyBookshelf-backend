package org.wsp.mybookshelf.domain.community.repository;

import org.wsp.mybookshelf.domain.community.entity.Comment;
import org.wsp.mybookshelf.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    void deleteByPostId(Long postId);
}