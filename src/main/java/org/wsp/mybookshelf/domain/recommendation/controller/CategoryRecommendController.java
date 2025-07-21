//package org.wsp.mybookshelf.domain.recommendation.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.json.JSONException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.wsp.mybookshelf.domain.bookshelf.dto.BookShelfDTO;
//import org.wsp.mybookshelf.domain.user.entity.User;
//import org.wsp.mybookshelf.domain.user.service.UserService;
//import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
//import org.wsp.mybookshelf.domain.recommendation.service.CategoryRecommendService;
//import org.wsp.mybookshelf.domain.recommendation.service.LoanBookService;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/recommend")
//@RequiredArgsConstructor
//public class CategoryRecommendController {
//
//    private final CategoryRecommendService categoryRecommendService;
//    private final LoanBookService loanBookService;
//    private final UserService userService;
//
//    @PostMapping("/popular-books/{userId}")
//    public ResponseEntity<List<Map<String, Object>>> getRecommendedBooksByUser(@PathVariable Long userId, @RequestBody BookResponse bookResponse) throws JSONException {
//        User user = userService.findUserById(userId);
//        int userAge = userService.getUserAge(user.getBirthDate().toString());
//
//        categoryRecommendService.printCategoryRow(bookResponse);
//        String kdcCode = categoryRecommendService.getKdcByCategoryId(bookResponse.getCategoryId());
//
//        if (kdcCode == null) {
//            return ResponseEntity.badRequest()
//                    .body(List.of(Map.of("error", "해당 categoryId에 대응되는 KDC 코드가 없습니다.")));
//        }
//
//        List<Map<String, Object>> books = loanBookService.getPopularBooksByKdc(kdcCode, userAge, 10);
//
//        return ResponseEntity.ok(books);
//    }
//
//    @GetMapping("/bookshelf/{userId}")
//    public ResponseEntity<List<BookShelfDTO>> getUserBookShelves(@PathVariable Long userId) throws JSONException {
//        List<BookShelfDTO> shelves = categoryRecommendService.getUserBookShelfWithBooks(userId);
//        return ResponseEntity.ok(shelves);
//    }
//}