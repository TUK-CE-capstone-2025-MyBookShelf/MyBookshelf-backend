    package org.wsp.mybookshelf.domain.book.dto;

    import lombok.*;

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class BookScoreDTO {
        private String title;
        private String author;
        private String isbn;
        private String cover;
        private String categoryName;
        private Integer categoryId;

        private int loanCount;

        private double weightedRatingScore; //가중 평점
        private double bestsellerScore; //베스트셀러
        private double duplicateScore; //중복 점수
        private double keywordScore; //키워드 점수
        private double similarityScore; //장르 유사도 점수
        private double preferenceScore; //선호 장르 점수
        private double loanScore; //대출 점수

        private int duplicateCount;

        //총점
        private double totalScore;

        public BookScoreDTO(BookRecommendDTO book) {
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.isbn = book.getIsbn();
            this.cover = book.getCover();
            this.categoryId = book.getCategoryId();
            this.categoryName = book.getCategoryName();
        }

    }
