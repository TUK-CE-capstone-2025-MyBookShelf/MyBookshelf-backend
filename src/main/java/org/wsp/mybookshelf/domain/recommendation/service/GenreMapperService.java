package org.wsp.mybookshelf.domain.recommendation.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.domain.book.dto.BookScoreDTO;
import org.wsp.mybookshelf.domain.user.service.UserService;
import org.wsp.mybookshelf.global.commonEntity.enums.Genre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GenreMapperService {

    private final Map<Integer, Genre> categoryToGenreMap = new HashMap<>();
    private final Map<Genre, List<Integer>> topCategoryMap = new HashMap<>();

    @PostConstruct
    public void loadGenreMap() {
        // 1. 일반 GenreMapper.csv 로딩
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("GenreMapper.csv").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                int cid = Integer.parseInt(parts[1].trim());
                String genreName = parts[5].trim();

                try {
                    Genre genre = Genre.valueOf(genreName);
                    categoryToGenreMap.put(cid, genre);
                } catch (IllegalArgumentException e) {
                    System.err.println("⚠️ Genre 매핑 실패: " + genreName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("GenreMapper.csv 로딩 실패", e);
        }

        // 2. 중요 카테고리 GenreTopCategories.csv 로딩
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("GenreTopCategories.csv").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String genreName = parts[0].trim();
                int categoryId = Integer.parseInt(parts[1].trim());

                try {
                    Genre genre = Genre.valueOf(genreName);
                    topCategoryMap.computeIfAbsent(genre, k -> new ArrayList<>()).add(categoryId);
                } catch (IllegalArgumentException e) {
                    System.err.println("⚠️ 중요 카테고리 장르 매핑 실패: " + genreName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("GenreTopCategories.csv 로딩 실패", e);
        }
    }

    public Genre getGenreByCategoryId(Integer categoryId) {
        return categoryToGenreMap.getOrDefault(categoryId, Genre.UNKNOWN);
    }

    public List<Integer> getTopCategoryIdsByGenre(Genre genre) {
        return topCategoryMap.getOrDefault(genre, Collections.emptyList());
    }
}


