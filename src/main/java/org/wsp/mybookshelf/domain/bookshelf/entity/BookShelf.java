package org.wsp.mybookshelf.domain.bookshelf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "BookShelf")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookShelf {

    @Id
    @Column(name = "bookshelf_id", nullable = false)
    private String bookshelfId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "status")
    private String status;
}
