package org.wsp.mybookshelf.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.global.commonEntity.enums.Genre;

@Entity
@Table(name = "UserGenre")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_genre_id", nullable = false)
    private Long userGenreId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;
}