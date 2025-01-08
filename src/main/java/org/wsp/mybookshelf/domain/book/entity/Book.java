package org.wsp.mybookshelf.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "Book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @Column(name = "book_id", nullable = false)
    private String bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "published_date")
    private Date publishedDate;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "category")
    private String category;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "description")
    private String description;

    @Column(name = "page")
    private Integer page;
}
