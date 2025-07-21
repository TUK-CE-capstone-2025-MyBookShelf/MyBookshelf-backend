package org.wsp.mybookshelf.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.book.repository.BookRepository;
import org.wsp.mybookshelf.domain.review.dto.BookReviewDTO;
import org.wsp.mybookshelf.domain.review.entity.BookReview;
import org.wsp.mybookshelf.domain.review.repository.BookReviewRepository;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class BookReviewService {

    private final BookReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public BookReviewDTO createReview(BookReviewDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("도서를 찾을 수 없습니다."));

        BookReview review = BookReview.builder()
                .user(user)
                .book(book)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        BookReview saved = reviewRepository.save(review);

        return toDTO(saved);
    }

    public BookReviewDTO getReview(Long id) {
        BookReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        return toDTO(review);
    }

    @Transactional
    public BookReviewDTO updateReview(Long id, BookReviewDTO dto) {
        BookReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());

        return toDTO(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private BookReviewDTO toDTO(BookReview review) {
        return BookReviewDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUser().getUserId())
                .bookId(review.getBook().getBookId())
                .title(review.getTitle())
                .content(review.getContent())
                .build();
    }
}
