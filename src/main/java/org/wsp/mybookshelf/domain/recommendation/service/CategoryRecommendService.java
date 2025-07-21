package org.wsp.mybookshelf.domain.recommendation.service;

import jakarta.annotation.PostConstruct;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.domain.book.dto.BookDTO;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.domain.bookshelf.dto.BookShelfDTO;
import org.wsp.mybookshelf.domain.bookshelf.service.BookShelfService;
import org.wsp.mybookshelf.domain.user.service.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CategoryRecommendService {

    private final Map<Integer, String[]> categoryMap = new HashMap<>();
    private final BookShelfService bookShelfService;
    private final LoanBookService loanBookService;
    private final UserService userService;

    public CategoryRecommendService(BookShelfService bookShelfService, LoanBookService loanBookService, UserService userService) {
        this.bookShelfService = bookShelfService;
        this.loanBookService = loanBookService;
        this.userService = userService;
    }


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


    public void printCategoryRow(BookResponse bookResponse) {
        Integer categoryId = bookResponse.getCategoryId();
        String[] row = categoryMap.get(categoryId);
        if (row != null) {
            System.out.println("카테고리 행 정보:");
            for (int i = 0; i < Math.min(row.length, 4); i++) {
                System.out.println((i + 1) + "열: " + row[i]);
            }
        } else {
            System.out.println("해당 categoryId에 대한 정보가 없습니다: " + categoryId);
        }
    }

//    public List<BookShelfDTO> getUserBookShelfWithBooks(Long userId) throws JSONException {
//        List<BookShelfDTO> userBookshelves = bookShelfService.getBookShelf(userId);
//        System.out.println("[사용자 ID: " + userId + "]의 책장 수: " + userBookshelves.size());
//
//        User user = userService.findUserById(userId);
//        int userAge = userService.getUserAge(user.getBirthDate().toString());
//
//        Map<String, Integer> kdcCountMap = new HashMap<>();
//
//        for (BookShelfDTO shelf : userBookshelves) {
//            System.out.println("책장 이름: " + shelf.getBookshelfName());
//            if (shelf.getBook() != null) {
//                System.out.println("책 수: " + shelf.getBook().size());
//
//                for (BookDTO book : shelf.getBook()) {
//                    if (book.getCategoryId() != null) {
//                        String kdc = getKdcByCategoryId(book.getCategoryId());
//                        if (kdc != null) {
//                            kdcCountMap.put(kdc, kdcCountMap.getOrDefault(kdc, 0) + 1);
//                        }
//                    }
//                }
//            }
//        }
//
//        System.out.println("책장에서 추출된 KDC 코드: " + kdcCountMap);
//
//        for (Map.Entry<String, Integer> entry : kdcCountMap.entrySet()) {
//            String kdc = entry.getKey();
//            int count = entry.getValue();
//            int pageSize = count * 10;
//
//            System.out.println("[추천 도서 검색 - KDC: " + kdc + " | 요청 개수: " + pageSize + "]");
//            List<Map<String, Object>> books = loanBookService.getPopularBooksByKdc(kdc, userAge, pageSize);
//            for (Map<String, Object> book : books) {
//                System.out.println(" - " + book.get("bookname") + " (" + book.get("authors") + ")");
//            }
//        }
//
//        return userBookshelves;
//    }
}