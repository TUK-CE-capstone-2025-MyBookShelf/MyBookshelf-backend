package org.wsp.mybookshelf.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wsp.mybookshelf.domain.review.entity.BookReview;

public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
}
