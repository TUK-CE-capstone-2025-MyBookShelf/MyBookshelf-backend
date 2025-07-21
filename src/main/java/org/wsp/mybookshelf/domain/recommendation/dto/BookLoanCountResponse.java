package org.wsp.mybookshelf.domain.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanCountResponse {
    private String isbn;
    private int LoanCount;
    private double LoanScore;
}