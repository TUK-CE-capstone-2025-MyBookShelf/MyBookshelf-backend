package org.wsp.mybookshelf.domain.recommendation.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CategoryGroupService {

    private final Map<String, Integer> categoryGroupMap = new HashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/CategoryMapping.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith(",")) continue;
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    String cid = tokens[1].trim();     // CID
                    int group = Integer.parseInt(tokens[2].trim()); // Group 번호
                    categoryGroupMap.put(cid, group);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("카테고리 그룹 매핑 CSV를 읽을 수 없습니다.", e);
        }
    }

    public Map<String, Integer> loadCategoryGroupMap() {
        return categoryGroupMap;
    }
}
