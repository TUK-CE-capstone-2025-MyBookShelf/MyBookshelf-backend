package org.wsp.mybookshelf.domain.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookReportRequestDTO {
    private Long bookId;
    private String title;
    private String content;
}
