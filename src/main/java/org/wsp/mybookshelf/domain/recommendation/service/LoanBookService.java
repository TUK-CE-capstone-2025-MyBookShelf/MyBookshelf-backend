package org.wsp.mybookshelf.domain.recommendation.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.wsp.mybookshelf.domain.recommendation.dto.BookLoanCountResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;

import java.time.LocalDate;
import java.util.*;


@Service
public class LoanBookService {

    @Value("${library.api.authKey}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<BookResponse> getPopularBooksByKdc(String kdc, int userAge, int pageSize) throws JSONException {
        String url = UriComponentsBuilder
                .fromHttpUrl("http://data4library.kr/api/loanItemSrch")
                .queryParam("authKey", apiKey)
                .queryParam("startDt", "2020-01-01")
                .queryParam("endDt", LocalDate.now().toString())
                .queryParam("dtl_kdc", kdc)
                .queryParam("pageNo", "1")
                .queryParam("pageSize", pageSize)
                .queryParam("age", userAge)
                .queryParam("format", "json")
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);
        JSONObject res = json.getJSONObject("response");

        System.out.println("URL : " + url);

        if (!res.has("docs")) {
            return Collections.emptyList();
        }

        JSONArray docs = res.getJSONArray("docs");
        List<BookResponse> result = new ArrayList<>();

        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

            BookResponse book = new BookResponse();

            book.setTitle(doc.optString("bookname"));
            book.setAuthor(doc.optString("authors"));
            book.setPublisher(doc.optString("publisher"));
            book.setIsbn(doc.optString("isbn13"));
            book.setCategoryName(doc.optString("class_nm"));
            book.setCover(doc.optString("bookImageURL"));

            result.add(book);
        }

        return result;
    }

    public List<BookLoanCountResponse> fetchBookLoans(List<String> isbns) {
        List<BookLoanCountResponse> loanCount = new ArrayList<>();

        for (String isbn : isbns) {
            String url = UriComponentsBuilder
                    .fromHttpUrl("http://data4library.kr/api/srchDtlList")
                    .queryParam("authKey", apiKey)
                    .queryParam("isbn13", isbn)
                    .queryParam("loaninfoYN", "Y")
                    .queryParam("displayInfo", "age")
                    .queryParam("format", "json")
                    .toUriString();

            try {
                String response = restTemplate.getForObject(url, String.class);

                JSONObject json = new JSONObject(response);
                JSONObject res = json.getJSONObject("response");

                BookLoanCountResponse bookLoan = new BookLoanCountResponse();
                bookLoan.setIsbn(isbn);
                bookLoan.setLoanScore(0.0);  // ✅ 기본 0.0

                int totalLoanCount = 0; // 초기화

                if (res.has("loanInfo")) {
                    JSONArray loanInfo = res.getJSONArray("loanInfo");

                    if (loanInfo.length() > 0) {
                        // 1. Total이 정상적으로 있는지 확인
                        Object totalObjRaw = loanInfo.getJSONObject(0).opt("Total");

                        if (totalObjRaw instanceof JSONObject totalObj) {
                            totalLoanCount = totalObj.optInt("loanCnt", 0);
                        } else {
                            // 2. Total이 비었으면 ageResult 검사
                            if (loanInfo.length() > 1 && loanInfo.getJSONObject(1).has("ageResult")) {
                                Object ageResultObj = loanInfo.getJSONObject(1).get("ageResult");
                                if (ageResultObj instanceof JSONArray ageResult) {
                                    for (int j = 0; j < ageResult.length(); j++) {
                                        JSONObject ageObj = ageResult.getJSONObject(j).optJSONObject("age");
                                        if (ageObj != null) {
                                            totalLoanCount += ageObj.optInt("loanCnt", 0);
                                        }
                                    }
                                } else {
                                    //System.err.println("ageResult is not an array for ISBN: " + isbn);
                                    continue;
                                }
                            } else {
                                //System.err.println("No Total and no ageResult for ISBN: " + isbn);
                                continue;
                            }
                        }
                    }
                }

                // 결과 세팅
                bookLoan.setLoanCount(totalLoanCount);

                loanCount.add(bookLoan);

            } catch (Exception e) {
                System.err.println("정보나루 API 요청 실패 (ISBN: " + isbn + ") - " + e.getMessage());
                System.out.println("URL : " + url);
            }
        }
        return loanCount;
    }
}