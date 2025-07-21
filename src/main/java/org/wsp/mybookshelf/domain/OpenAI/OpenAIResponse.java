package org.wsp.mybookshelf.domain.OpenAI;

import lombok.Data;

@Data
public class OpenAIResponse {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
}