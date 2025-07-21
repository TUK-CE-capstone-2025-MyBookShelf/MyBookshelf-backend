package org.wsp.mybookshelf.global.searchApi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRatingResponse {
    private String isbn;          // ISBN 코드
    private double ratingScore;      // 고객 평점
    private int ratingCount;      // 리뷰 개수
}