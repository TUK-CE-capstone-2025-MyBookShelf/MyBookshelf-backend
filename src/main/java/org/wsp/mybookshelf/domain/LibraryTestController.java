package org.wsp.mybookshelf.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@RestController
@RequestMapping("/api/test")
public class LibraryTestController {

    private static final String API_KEY = "ac27b83efe6300bdaca35e85cd0dcbb9d25b040859d2cc266f60617e783a9fa2";
    private static final String BASE_URL = "https://data4library.kr/api";

    private final RestTemplate restTemplate;

    public LibraryTestController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @GetMapping("/keyword")
    public ResponseEntity<?> testKeywordSearch(@RequestParam String keyword) {
        try {
            String url = BASE_URL + "/srchBooks?authKey=" + API_KEY + "&keyword=" + keyword + "&pageSize=5&format=json";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 오류 발생: " + e.getMessage());
        }
    }

}
