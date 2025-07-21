    package org.wsp.mybookshelf.global.searchApi.service;

    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.wsp.mybookshelf.global.searchApi.dto.BookRatingResponse;
    import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;

    @Service
    public class AladinService {
        private final RestTemplate restTemplate;
        private final String API_KEY = "ttbywoo97971149001"; // API 키 입력

        //private final String API_KEY = "ttbkang456dh06060322001"; // API 키 입력

        //private final String API_KEY = "ttbjjojin71682109001"; // API 키 입력
        private static final int MIN_REVIEWS = 0;

        public AladinService(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public List<BookResponse> searchBooks(String query, String queryType, String start, int maxResults, boolean showDetail) {
            String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?TTBKey=" + API_KEY +
                    "&QueryType=" + queryType +
                    "&Query=" + query +
                    "&MaxResults=" + maxResults +
                    "&Cover=Big" +
                    //"&Start=" + start +
                    "&SearchTarget=Book&Output=JS&Version=20131101";

            Map response = restTemplate.getForObject(url, Map.class);
            List<BookResponse> books = new ArrayList<>();

            if (response != null && response.containsKey("item")) {
                List<Map> items = (List<Map>) response.get("item");
                for (Map item : items) {
                    BookResponse book = new BookResponse();
                    book.setTitle((String) item.get("title"));
                    book.setAuthor((String) item.get("author"));
                    book.setPublisher((String) item.get("publisher"));

                    book.setCategoryName((String) item.get("categoryName"));
                    book.setCategoryId((Integer) item.get("categoryId"));

                    book.setIsbn((String) item.get("isbn13"));
                    book.setCover((String) item.get("cover"));
                    book.setPublicationDate((String) item.get("pubDate"));
                    book.setSource("Aladin API");

                    if (showDetail) {
                        book.setDescription((String) item.get("description")); // 상세 검색 시만 포함
                    }

                    books.add(book);
                }
            }
            return books;
        }

        public BookResponse searchBookDetail(String isbn13) {
            String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?TTBKey=" + API_KEY +
                    "&Query=" + isbn13 +
                    "&Cover=Big" +
                    "&SearchTarget=Book&Output=JS&Version=20131101";

            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("item")) {
                List<Map> items = (List<Map>) response.get("item");

                // ✅ 빈 리스트 처리
                if (items == null || items.isEmpty()) {
                    System.out.println("❗ Aladin API 결과 없음 - ISBN: " + isbn13);
                    return null;
                }

                Map item = items.get(0);  // 안전하게 get(0)

                BookResponse bookResponse = new BookResponse();
                bookResponse.setTitle((String) item.get("title"));
                bookResponse.setAuthor((String) item.get("author"));
                bookResponse.setPublisher((String) item.get("publisher"));
                bookResponse.setIsbn((String) item.get("isbn13"));
                bookResponse.setCover((String) item.get("cover"));
                bookResponse.setCustomerReviewRank((Integer) item.get("customerReviewRank"));
                bookResponse.setSource("Aladin API");
                bookResponse.setCategoryId((Integer) item.get("categoryId"));
                bookResponse.setDescription((String) item.get("description"));
                bookResponse.setCategoryName((String) item.get("categoryName"));

                return bookResponse;
            }

            // ❗ 응답이 null이거나 item key가 없음
            System.out.println("❗ Aladin API 응답 오류 또는 item 없음 - ISBN: " + isbn13);
            return null;
        }

        public BookResponse searchBookCover(String isbn13) {
            String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?TTBKey=" + API_KEY +
                    "&Query=" + isbn13 +
                    "&SearchTarget=Book&Output=JS&Version=20131101";

            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("item")) {
                List<Map> items = (List<Map>) response.get("item");

                // ✅ 빈 리스트 처리
                if (items == null || items.isEmpty()) {
                    System.out.println("❗ Aladin API 결과 없음 - ISBN: " + isbn13);
                    return null;
                }

                Map item = items.get(0);  // 안전하게 get(0)

                BookResponse bookResponse = new BookResponse();
                bookResponse.setTitle((String) item.get("title"));
                bookResponse.setAuthor((String) item.get("author"));
                bookResponse.setPublisher((String) item.get("publisher"));
                bookResponse.setIsbn((String) item.get("isbn13"));
                bookResponse.setCover((String) item.get("cover"));
                bookResponse.setCustomerReviewRank((Integer) item.get("customerReviewRank"));
                bookResponse.setSource("Aladin API");
                bookResponse.setCategoryId((Integer) item.get("categoryId"));
                bookResponse.setDescription((String) item.get("description"));
                bookResponse.setCategoryName((String) item.get("categoryName"));

                return bookResponse;
            }

            // ❗ 응답이 null이거나 item key가 없음
            System.out.println("❗ Aladin API 응답 오류 또는 item 없음 - ISBN: " + isbn13);
            return null;
        }


        public List<BookResponse> searchBooksByCategory(int categoryId, int maxResults) {
            String url = "https://www.aladin.co.kr/ttb/api/ItemList.aspx?TTBKey=" + API_KEY +
                    "&QueryType=" + "Bestseller" +
                    "&CategoryId=" + categoryId +
                    "&MaxResults=" + maxResults +
                    "&SearchTarget=Book&Output=JS&Version=20131101";

            System.out.println("[API 요청 URL]: " + url);

            Map response = restTemplate.getForObject(url, Map.class);
            List<BookResponse> books = new ArrayList<>();

            System.out.println("[API 응답]: " + response);

            if (response != null && response.containsKey("item")) {
                List<Map> items = (List<Map>) response.get("item");

                System.out.println("[응답 아이템 개수]: " + items.size());

                for (Map item : items) {
                    int rating = (Integer) item.getOrDefault("customerReviewRank", 0); // 평점이 없으면 기본값 0

                    // 평점이 7 이상인 도서만 추가
                    if (rating >= 7) {
                        System.out.println("[도서 정보] 제목: " + item.get("title") + ", 평점: " + rating);

                        BookResponse book = new BookResponse();
                        book.setTitle((String) item.get("title"));
                        book.setAuthor((String) item.get("author"));
                        book.setPublisher((String) item.get("publisher"));
                        book.setCategoryName((String) item.get("categoryName"));
                        book.setCategoryId((Integer) item.get("categoryId"));
                        book.setIsbn((String) item.get("isbn13"));
                        book.setCover((String) item.get("cover"));
                        book.setPublicationDate((String) item.get("pubDate"));
                        book.setCustomerReviewRank(rating);
                        book.setSource("Aladin API");

                        books.add(book);
                    }
                }
            }

            System.out.println("[최종 반환 도서 개수]: " + books.size());
            return books;
        }

        public List<BookResponse> searchBooksByCategorySafe(int categoryId, int maxResults) {
            String url = "https://www.aladin.co.kr/ttb/api/ItemList.aspx?TTBKey=" + API_KEY +
                    "&QueryType=Bestseller" +
                    "&CategoryId=" + categoryId +
                    "&MaxResults=" + maxResults +
                    "&SearchTarget=Book&Output=JS&Version=20131101";

            System.out.println("[API 요청 URL]: " + url);

            List<BookResponse> books = new ArrayList<>();

            try {
                String responseBody = restTemplate.getForObject(url, String.class);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);

                JsonNode items = root.get("item");
                System.out.println("[응답 아이템 개수]: " + (items != null ? items.size() : 0));

                if (items != null && items.isArray()) {
                    for (JsonNode item : items) {
                        int rating = item.path("customerReviewRank").asInt(0);

                        // 평점 7 이상만
                        if (rating >= 7) {
                            BookResponse book = new BookResponse();
                            book.setTitle(item.path("title").asText());
                            book.setAuthor(item.path("author").asText());
                            book.setPublisher(item.path("publisher").asText());
                            book.setCategoryName(item.path("categoryName").asText());
                            book.setCategoryId(item.path("categoryId").asInt());
                            book.setIsbn(item.path("isbn13").asText());
                            book.setCover(item.path("cover").asText());
                            book.setPublicationDate(item.path("pubDate").asText());
                            book.setCustomerReviewRank(rating);
                            book.setSource("Aladin API");

                            books.add(book);

                            System.out.println("[도서 정보] 제목: " + book.getTitle() + ", 평점: " + rating);
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("[오류 발생]: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("[최종 반환 도서 개수]: " + books.size());
            return books;
        }


        public List<BookRatingResponse> fetchBookRatings(List<String> isbns) {
            List<BookRatingResponse> ratings = new ArrayList<>();

            for (String isbn : isbns) {
                String url = "https://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?TTBKey=" + API_KEY +
                        "&ItemIdType=ISBN" +
                        "&ItemId=" + isbn +
                        "&Output=JS" +
                        "&Version=20131101" +
                        "&OptResult=ratingInfo";  // 평점 정보 포함

                try {
                    Map response = restTemplate.getForObject(url, Map.class);
                    System.out.println("알라딘 API 응답 (ISBN: " + isbn + ") : " + response);

                    if (response != null && response.containsKey("item")) {
                        List<Map> items = (List<Map>) response.get("item");

                        if (!items.isEmpty()) {
                            Map item = items.get(0);

                            // subInfo 안에 ratingInfo가 존재하는지 확인
                            Map subInfo = (Map) item.get("subInfo");
                            Map ratingInfo = subInfo != null ? (Map) subInfo.get("ratingInfo") : null;

                            double ratingScore = ratingInfo != null && ratingInfo.containsKey("ratingScore")
                                    ? ((Number) ratingInfo.get("ratingScore")).doubleValue()
                                    : 0.0;

                            int ratingCount = ratingInfo != null && ratingInfo.containsKey("ratingCount")
                                    ? ((Number) ratingInfo.get("ratingCount")).intValue()
                                    : 0;

                            // 결과 저장
                            BookRatingResponse ratingResponse = new BookRatingResponse();
                            ratingResponse.setIsbn(isbn);
                            ratingResponse.setRatingScore(ratingScore);
                            ratingResponse.setRatingCount(ratingCount);

                            ratings.add(ratingResponse);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("알라딘 API 요청 실패 (ISBN: " + isbn + ") - " + e.getMessage());
                }
            }
            return ratings;
        }

        //전체 베스트셀러 정보
        public Integer getBestsellerRank(String isbn) {
            String url = "https://www.aladin.co.kr/ttb/api/ItemList.aspx?TTBKey=" + API_KEY +
                    "&QueryType=Bestseller" +
                    "&MaxResults=100" +
                    "&SearchTarget=Book&Output=JS&Version=20131101";

            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("item")) {
                List<Map> items = (List<Map>) response.get("item");
                for (int i = 0; i < items.size(); i++) {
                    Map item = items.get(i);
                    String itemIsbn13 = (String) item.get("isbn13"); // 이 부분 수정
                    if (isbn.equals(itemIsbn13)) {
                        return i + 1;
                    }
                }
            }

            return null;
        }






    }