package org.wsp.mybookshelf.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 전략 설정
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "category_id")  //카테고리 ID
    private Integer categoryId;
    
    @Column(name = "category_name") //카테고리 이름
    private String categoryName;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "published_date") // 출판일로 변경
    private String publicationDate;

    @Column(name = "thumbnail") // 책 표지로 변경
    private String cover;

    @Column(name = "customer_review_rank")
    private Integer customerReviewRank;

    @Column(name = "source") // 데이터 출처 추가
    private String source;

    @Column(name = "description")
    private String description;
}
