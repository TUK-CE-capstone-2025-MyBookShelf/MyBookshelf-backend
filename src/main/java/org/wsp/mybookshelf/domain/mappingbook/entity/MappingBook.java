package org.wsp.mybookshelf.domain.mappingbook.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.bookshelf.entity.BookShelf;

@Entity
@Table(name = "MappingBook")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MappingBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 전략 설정
    @Column(name = "mapping_id", nullable = false)
    private Long mappingId;

    @ManyToOne // Book과의 Many-To-One 관계
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; //

    @ManyToOne // BookShelf과의 Many-To-One 관계
    @JoinColumn(name = "bookshelf_id", nullable = false)
    private BookShelf bookshelf; //
}