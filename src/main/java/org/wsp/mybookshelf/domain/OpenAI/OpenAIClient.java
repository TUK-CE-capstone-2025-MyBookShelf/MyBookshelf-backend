package org.wsp.mybookshelf.domain.OpenAI;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OpenAIClient {

    private final RestTemplate restTemplate;

    // 🔹 application.properties 또는 application.yml에서 API 키 자동 주입
    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${openai.enabled:false}")
    private boolean enabled;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public boolean isEnabled() {
        return enabled && openAiApiKey != null && !openAiApiKey.isBlank();
    }

    public String getCompletion(String prompt, String model) {
        if (!isEnabled()) return "";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openAiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "너는 도서 추천 전문가야."));
        messages.add(Map.of("role", "user", "content", prompt));

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_API_URL, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> choice = ((List<Map<String, Object>>) response.getBody().get("choices")).get(0);
                return (String) ((Map<String, Object>) choice.get("message")).get("content");
            } else {
                System.out.println("⚠️ OpenAI API 응답 오류: " + response.getStatusCode());
                return "";
            }
        } catch (Exception e) {
            System.out.println("❌ OpenAI API 호출 실패: " + e.getMessage());
            return "";
        }
    }
}
