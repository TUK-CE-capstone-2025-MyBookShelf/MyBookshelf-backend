package org.wsp.mybookshelf.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wsp.mybookshelf.domain.community.entity.BoardType;
import org.wsp.mybookshelf.domain.community.entity.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시판 타입별 게시글 목록 (최신순)
    List<Post> findByBoardTypeOrderByCreatedAtDesc(BoardType boardType);
}
