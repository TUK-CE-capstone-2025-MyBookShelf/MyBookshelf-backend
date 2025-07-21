package org.wsp.mybookshelf.domain.recommendation.service;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class RecommendCategoryMappingService {

    private final Map<Integer, String[]> categoryMap = new HashMap<>();

    @PostConstruct
    public void loadCategoryMappings() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("AladinToLibrary.csv"))))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int categoryId = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                        categoryMap.put(categoryId, parts);
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 숫자 형식: " + parts[0]);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKdcByCategoryId(Integer categoryId) {
        String[] row = categoryMap.get(categoryId);
        if (row != null && row.length >= 4) {
            String kdc = row[2];
            // 숫자로 시작하는 것만 유효한 KDC로 간주
            if (kdc.matches("\\d+")) {
                return kdc;
            }
        }
        return null; // 유효한 KDC가 아닌 경우 null 반환
    }
}