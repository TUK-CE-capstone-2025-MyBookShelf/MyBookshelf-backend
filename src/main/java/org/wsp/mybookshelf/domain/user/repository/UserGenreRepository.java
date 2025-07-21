package org.wsp.mybookshelf.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wsp.mybookshelf.domain.user.entity.UserGenre;
import org.wsp.mybookshelf.global.commonEntity.enums.Genre;

import java.util.List;

@Repository
public interface UserGenreRepository extends JpaRepository<UserGenre, Long> {

    List<UserGenre> findByUserId(Long userId);

}
