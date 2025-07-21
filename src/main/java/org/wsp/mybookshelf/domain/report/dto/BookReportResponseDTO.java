package org.wsp.mybookshelf.domain.report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class BookReportResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String bookTitle;
    private String userNickname;
    private String createdAt;
}
