package org.wsp.mybookshelf.domain.recommendation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.domain.OpenAI.OpenAIService;
import org.wsp.mybookshelf.domain.book.dto.*;
import org.wsp.mybookshelf.domain.bookshelf.service.BookShelfService;
import org.wsp.mybookshelf.domain.recommendation.dto.BookLoanCountResponse;
import org.wsp.mybookshelf.domain.user.entity.UserGenre;
import org.wsp.mybookshelf.domain.user.repository.UserGenreRepository;
import org.wsp.mybookshelf.domain.user.service.UserService;
import org.wsp.mybookshelf.global.commonEntity.enums.Genre;
import org.wsp.mybookshelf.global.searchApi.dto.BookRatingResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;
import org.wsp.mybookshelf.global.searchApi.service.LibraryService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecommendationService {

    @Autowired
    private BookShelfService bookShelfService;
    @Autowired
    private AladinService aladinService;
    @Autowired
    private LibraryService libraryService;
    @Autowired
    private OpenAIService openAIService;
    @Autowired
    private LoanBookService loanBookService;
    @Autowired
    private CategoryGroupService categoryGroupService;
    @Autowired
    private UserService userService;
    @Autowired
    private GenreMapperService genreMapperService;
    @Autowired
    private UserGenreRepository userGenreRepository;

    private static final int M = 10;

    public List<BookRecommendDTO> getRatingBasedCandidates(Long bookshelfId) {
        List<BookResponse> recommendedBooks = bookShelfService.getRecommendedBooksByCategory(bookshelfId);
        Set<String> existingIsbns = new HashSet<>(bookShelfService.getIsbn(bookshelfId));

        List<BookResponse> filteredBooks = recommendedBooks.stream()
                .filter(book -> !existingIsbns.contains(book.getIsbn()))
                .toList();

        Set<String> isbns = filteredBooks.stream().map(BookResponse::getIsbn).collect(Collectors.toSet());
        List<BookRatingResponse> ratings = aladinService.fetchBookRatings(new ArrayList<>(isbns));
        List<BookRatingResponse> weighted = applyWeightedRating(ratings);

        return weighted.stream().map(r -> {
            BookResponse b = filteredBooks.stream()
                    .filter(book -> book.getIsbn().equals(r.getIsbn()))
                    .findFirst().orElse(null);
            if (b == null) return null;
            return new BookRecommendDTO(b.getTitle(), b.getAuthor(), b.getIsbn(), r.getRatingScore(), b.getCover(), b.getCategoryName(), b.getCategoryId());
        }).filter(Objects::nonNull).toList();
    }


    public List<String> getAllKeywordsFromBooks(List<String> isbns) {
        List<String> keywords = new ArrayList<>();
        for (String isbn : isbns) {
            BookKeywordResponse response = libraryService.getBookKeywords(isbn);
            List<BookKeywordResponse.Keyword> keywordList = response != null ? response.getKeywords() : new ArrayList<>();

            // 키워드 10개까지 GPT로 보완
            if (keywordList.size() < 10) {
                List<BookKeywordResponse.Keyword> extra = openAIService.generateMissingKeywords(isbn, 10 - keywordList.size());
                keywordList.addAll(extra);
            }

            // 최대 10개까지만 수집
            keywords.addAll(keywordList.stream().map(BookKeywordResponse.Keyword::getWord).limit(10).toList());
        }
        return keywords;
    }


    public List<BookRecommendDTO> getKeywordBasedCandidates(Long bookshelfId) {
        Set<String> existing = new HashSet<>(bookShelfService.getIsbn(bookshelfId));
        Set<String> seenTitles = new HashSet<>();
        List<BookRecommendDTO> results = new ArrayList<>();
        List<String> isbns = new ArrayList<>(existing);

        for (String isbn : isbns) {
            BookKeywordResponse response = libraryService.getBookKeywords(isbn);
            List<BookKeywordResponse.Keyword> keywords = response != null ? response.getKeywords() : new ArrayList<>();
            if (keywords.size() < 5) {
                keywords.addAll(openAIService.generateMissingKeywords(isbn, 5 - keywords.size()));
            }

            for (BookKeywordResponse.Keyword k : keywords.stream().limit(5).toList()) {
                List<BookCandidateDTO> found = libraryService.searchBooksByKeyword(k.getWord(), 5);
                for (BookCandidateDTO book : found) {
                    if (!existing.contains(book.getIsbn()) && !seenTitles.contains(book.getTitle())) {
                        seenTitles.add(book.getTitle());
                        results.add(new BookRecommendDTO(
                                book.getTitle(),
                                book.getAuthor(),
                                book.getIsbn(),
                                0.0, // 평점 0
                                "",  // 커버 없음 (원하면 커버도 검색 결과에 추가할 수 있음)
                                book.getCategoryName(), // ✅ 제대로 카테고리명
                                book.getCategoryId()    // ✅ 카테고리ID 추가
                        ));

                    }
                }
            }
        }

        return results;
    }

    public List<BookRecommendDTO> getCombinedRecommendationCandidates(Long bookshelfId) {
        List<BookRecommendDTO> rating = getRatingBasedCandidates(bookshelfId);
        List<BookRecommendDTO> keyword = getKeywordBasedCandidates(bookshelfId);
        List<BookRecommendDTO> loan = getCategoryBasedCandidates(bookshelfId);

        Map<String, BookRecommendDTO> map = new LinkedHashMap<>();

        for (BookRecommendDTO dto : Stream.of(rating, keyword, loan)
                .flatMap(List::stream)
                .toList()) {
            map.putIfAbsent(dto.getIsbn(), dto);
        }

        return new ArrayList<>(map.values());
    }

    public List<BookRecommendDTO> getCombinedRecommendationCandidatesTest(Long bookshelfId) {
        List<BookRecommendDTO> rating = getRatingBasedCandidates(bookshelfId);
        //List<BookRecommendDTO> keyword = getKeywordBasedCandidates(bookshelfId);
        List<BookRecommendDTO> loan = getCategoryBasedCandidates(bookshelfId);

        // 두 가지 리스트(rating + loan)를 합침
        List<BookRecommendDTO> combinedList = Stream.concat(rating.stream(), loan.stream())
                .collect(Collectors.toList());

        // for Debug
        System.out.println("Combined List size: " + combinedList.size());
//    for(BookRecommendDTO brDTO : combinedList) {
//        System.out.println("CombinedList: " + brDTO);
//    }

        // 결과 반환
        return combinedList;
    }

    // test
    public List<BookScoreDTO> applyWeightedRatingToBooksForTest(List<BookRecommendDTO> candidates) {
        List<String> isbns = candidates.stream().map(BookRecommendDTO::getIsbn).filter(Objects::nonNull).distinct().toList();

        // for Debug
        // System.out.println("isbns Count: " + isbns.size());

        // for Test
        List<BookRatingResponse> ratingResponses = aladinService.fetchBookRatings(isbns);
        List<BookRatingResponse> weightedRate = applyWeightedRating(ratingResponses);
        Map<String, Double> rateMap = weightedRate.stream().collect(Collectors.toMap(BookRatingResponse::getIsbn, BookRatingResponse::getRatingScore));

        List<BookLoanCountResponse> loanResponses = loanBookService.fetchBookLoans(isbns);
        List<BookLoanCountResponse> weightedLoan = applyLoanScore(loanResponses);
        Map<String, Double> loanMap = weightedLoan.stream().collect(Collectors.toMap(BookLoanCountResponse::getIsbn, BookLoanCountResponse::getLoanScore));

        return candidates.stream().map(b -> {
            BookScoreDTO dto = new BookScoreDTO();
            dto.setTitle(b.getTitle());
            dto.setAuthor(b.getAuthor());
            dto.setIsbn(b.getIsbn());
            dto.setCover(b.getCover());
            dto.setCategoryName(b.getCategoryName());
            dto.setWeightedRatingScore(rateMap.getOrDefault(b.getIsbn(), 0.0));
            dto.setLoanScore(loanMap.getOrDefault(b.getIsbn(), 0.0)); // for Test
            return dto;
        }).toList();
    }


    public List<BookRatingResponse> applyWeightedRating(List<BookRatingResponse> ratings) {
        double C = ratings.stream().mapToDouble(BookRatingResponse::getRatingScore).average().orElse(0.0);
        for (BookRatingResponse r : ratings) {
            double wr = (r.getRatingCount() / (double) (r.getRatingCount() + M)) * r.getRatingScore() +
                    (M / (double) (r.getRatingCount() + M)) * C;
            r.setRatingScore(wr);
        }
        ratings.sort(Comparator.comparing(BookRatingResponse::getRatingScore).reversed());
        return ratings;
    }

    public List<BookScoreDTO> applyWeightedRatingToBooks(List<BookRecommendDTO> candidates) {
        List<String> isbns = candidates.stream().map(BookRecommendDTO::getIsbn).filter(Objects::nonNull).distinct().toList();
        List<BookRatingResponse> ratingResponses = aladinService.fetchBookRatings(isbns);
        List<BookRatingResponse> weighted = applyWeightedRating(ratingResponses);
        Map<String, Double> map = weighted.stream().collect(Collectors.toMap(BookRatingResponse::getIsbn, BookRatingResponse::getRatingScore));

        return candidates.stream().map(b -> {
            BookScoreDTO dto = new BookScoreDTO();
            dto.setTitle(b.getTitle());
            dto.setAuthor(b.getAuthor());
            dto.setIsbn(b.getIsbn());
            dto.setCover(b.getCover());
            dto.setCategoryName(b.getCategoryName());
            dto.setCategoryId(b.getCategoryId());
            dto.setWeightedRatingScore(map.getOrDefault(b.getIsbn(), 0.0));
            return dto;
        }).toList();
    }

    public List<BookScoreDTO> applyBestsellerScore(List<BookScoreDTO> books) {
        for (BookScoreDTO book : books) {
            int rank = Optional.ofNullable(aladinService.getBestsellerRank(book.getIsbn())).orElse(999);
            double score = rank <= 10 ? 10 : rank <= 20 ? 9 : rank <= 30 ? 8 : rank <= 40 ? 7 : rank <= 50 ? 6 : rank <= 60 ? 5 : rank <= 70 ? 4 : 3;
            book.setBestsellerScore(score);
        }
        return books;
    }



    public List<BookScoreDTO> applyKeywordSimilarityScore(List<BookScoreDTO> candidates, List<String> keywords) {
        String prompt = openAIService.buildSimilarityPrompt(keywords, candidates);
        Map<String, Double> resultMap = openAIService.getKeywordSimilarityScore(prompt);
        for (BookScoreDTO dto : candidates) {
            dto.setKeywordScore(resultMap.getOrDefault(dto.getIsbn(), 0.0));
        }
        return candidates;
    }


    public List<BookScoreDTO> calculateTotalScore(List<BookScoreDTO> books) {
        for (BookScoreDTO book : books) {
            double total = 0.0;
            total += book.getWeightedRatingScore() * 0.3;
            total += book.getBestsellerScore() * 0.1;

            total += book.getSimilarityScore() * 0.25;       // ✅ 장르 유사 점수
            total += book.getPreferenceScore() * 0.2;       // ✅ 사용자 선호 장르 점수
            total += book.getKeywordScore() * 0.1;
            total += book.getLoanScore() * 0.05;
            book.setTotalScore(total);
        }
        return books;
    }


    //장르 유사도
    public List<BookScoreDTO> applyGenreSimilarityScore(List<BookScoreDTO> books, List<Integer> userCategoryIds) {
        Map<String, Integer> categoryGroupMap = categoryGroupService.loadCategoryGroupMap(); // CSV에서 Group 매핑
        List<BookScoreDTO> updatedBooks = new ArrayList<>();

        for (BookScoreDTO book : books) {
            Integer bookCategoryId = book.getCategoryId();

            // ✅ null일 경우 기본 점수 부여 후 continue
            if (bookCategoryId == null) {
                book.setSimilarityScore(6.0);
                updatedBooks.add(book);
                continue;
            }

            int bookGroup = categoryGroupMap.getOrDefault(bookCategoryId, -1);
            double score = 6.0;

            for (Integer userCategoryId : userCategoryIds) {
                if (userCategoryId == null) continue; // 혹시 모를 null 대비

                int userGroup = categoryGroupMap.getOrDefault(userCategoryId, -2);

                if (bookCategoryId.equals(userCategoryId)) {
                    score = 10.0;
                    break;
                } else if (bookGroup != -1 && bookGroup == userGroup) {
                    score = Math.max(score, 8.0);
                }
            }

            book.setSimilarityScore(score);
            updatedBooks.add(book);
        }

        return updatedBooks;
    }




    public List<BookScoreDTO> applyCategoryInfo(List<BookScoreDTO> books) {
        for (BookScoreDTO book : books) {
            BookResponse detail = aladinService.searchBookDetail(book.getIsbn());
            if (detail != null && detail.getCategoryId() != null) {
                book.setCategoryId(Integer.valueOf(String.valueOf(detail.getCategoryId())));
            }
        }
        return books;
    }

    public List<BookScoreDTO> applyMissingBookInfo(List<BookScoreDTO> books) {
        for (BookScoreDTO book : books) {
            boolean needsUpdate = book.getCover() == null || book.getCover().isBlank()
                    || book.getCategoryName() == null || book.getCategoryId() == null;

            if (needsUpdate) {
                BookResponse detail = aladinService.searchBookCover(book.getIsbn());

                if (detail != null) {
                    if ((book.getCover() == null || book.getCover().isBlank()) && detail.getCover() != null) {
                        book.setCover(detail.getCover());
                    }

                    if (book.getCategoryName() == null && detail.getCategoryName() != null) {
                        book.setCategoryName(detail.getCategoryName());
                    }

                    if (book.getCategoryId() == null && detail.getCategoryId() != null) {
                        book.setCategoryId(detail.getCategoryId());
                    }
                }
            }
        }

        return books;
    }


    public List<BookRecommendDTO> getCategoryBasedCandidates(Long bookshelfId) {
        List<BookResponse> recommendedBooks = bookShelfService.getRecommendedBooksByBookshelfId(bookshelfId);
        System.out.println("Check recommendBooks Count: " + recommendedBooks.size());
        Set<String> existingIsbns = new HashSet<>(bookShelfService.getIsbn(bookshelfId));

        List<BookResponse> filteredBooks = recommendedBooks.stream()
                .filter(book -> !existingIsbns.contains(book.getIsbn()))
                .toList();

        Set<String> isbns = filteredBooks.stream().map(BookResponse::getIsbn).collect(Collectors.toSet());
        List<BookLoanCountResponse> loanCount = loanBookService.fetchBookLoans(new ArrayList<>(isbns));
//        System.out.println("check loanCount, loanCount size : " + loanCount.size());
//        for(BookLoanCountResponse lC : loanCount){
//            System.out.println("loanCount - " + lC);
//        }

        List<BookLoanCountResponse> weighted = applyLoanScore(loanCount);
//        System.out.println("check weighted, weighted size : " + weighted.size());
//        for(BookLoanCountResponse w : weighted){
//            System.out.println("loanCount - " + w);
//        }

        return weighted.stream().map(r -> {
            BookResponse b = filteredBooks.stream()
                    .filter(book -> book.getIsbn().equals(r.getIsbn()))
                    .findFirst().orElse(null);
            if (b == null) return null;
            return new BookRecommendDTO(b.getTitle(), b.getAuthor(), b.getIsbn(), r.getLoanScore(), b.getCover(), b.getCategoryName(), b.getCategoryId());
        }).filter(Objects::nonNull).toList();
    }



    public List<BookLoanCountResponse> applyLoanScore(List<BookLoanCountResponse> loanCounts) {
        List<BookLoanCountResponse> result = new ArrayList<>();

        if (loanCounts.isEmpty()) {
            System.out.println("[DEBUG] loanCounts is empty.");
            return result;
        }

        int maxLoanCount = loanCounts.stream()
                .mapToInt(BookLoanCountResponse::getLoanCount)
                .max()
                .orElse(1);

        System.out.println("[DEBUG] maxLoanCount: " + maxLoanCount);

        for (BookLoanCountResponse loan : loanCounts) {
            int count = loan.getLoanCount();

            double weightedScore;
            if (count == 0) {
                weightedScore = 3.0;
            } else {
                weightedScore = (count / (double) maxLoanCount) * 10.0;
                if (weightedScore <= 3.0) weightedScore = 3.0;
            }

            System.out.printf("[DEBUG] ISBN: %s | count: %d | score: %.2f\n",
                    loan.getIsbn(), count, weightedScore);

            BookLoanCountResponse rating = new BookLoanCountResponse();
            rating.setIsbn(loan.getIsbn());
            rating.setLoanScore(weightedScore);
            rating.setLoanCount(count);

            result.add(rating);
        }

        return result;
    }


    public List<BookScoreDTO> applyLoanScoreDTO(List<BookScoreDTO> books) {
        List<String> isbns = books.stream()
                .map(BookScoreDTO::getIsbn)
                .toList();

        List<BookLoanCountResponse> loanCounts = loanBookService.fetchBookLoans(isbns);
        List<BookLoanCountResponse> scoredLoans = applyLoanScore(loanCounts); // 기존 메서드 재활용

        Map<String, Double> scoreMap = scoredLoans.stream()
                .collect(Collectors.toMap(BookLoanCountResponse::getIsbn, BookLoanCountResponse::getLoanScore));

        for (BookScoreDTO book : books) {
            Double score = scoreMap.get(book.getIsbn());
            book.setLoanScore(score != null ? score : 0.0);
        }

        return books;
    }


    //선호 장르 점수 계산
    public int calculatePreferredGenreScore(Genre bookGenre, List<Genre> userPreferredGenres) {
        if (bookGenre == null || bookGenre == Genre.UNKNOWN) {
            return 0;
        }
        return userPreferredGenres.contains(bookGenre) ? 10 : 5;
    }

    public List<BookScoreDTO> applyPreferenceScore(List<BookScoreDTO> books, Long bookshelfId) {
        List<Genre> preferredGenres = userService.getUserPreferredGenresByBookshelfId(bookshelfId);

        for (BookScoreDTO book : books) {
            Integer categoryId = book.getCategoryId();
            if (categoryId == null) {
                book.setPreferenceScore(5); // 기본 점수
                continue;
            }

            Genre bookGenre = genreMapperService.getGenreByCategoryId(categoryId); // ← 여기 수정
            int score = calculatePreferredGenreScore(bookGenre, preferredGenres);
            book.setPreferenceScore(score);
        }

        return books;
    }


    //회원정보로 선호 장르 점수 적용
    public List<BookScoreDTO> applyPreferenceScoreByUser(List<BookScoreDTO> books, Long userId) {
        List<Genre> preferredGenres = userService.getUserPreferredGenresByUserId(userId);

        for (BookScoreDTO book : books) {
            Integer categoryId = book.getCategoryId();
            if (categoryId == null) {
                book.setPreferenceScore(5); // 기본 점수
                continue;
            }

            Genre bookGenre = genreMapperService.getGenreByCategoryId(categoryId); // ← 여기 수정
            int score = calculatePreferredGenreScore(bookGenre, preferredGenres);
            book.setPreferenceScore(score);
        }

        return books;
    }


    public List<BookScoreDTO> recommendByUserPreferredGenres(Long userId) {
        // 1. 사용자 선호 장르 조회
        List<Genre> preferredGenres = userGenreRepository.findByUserId(userId).stream()
                .map(UserGenre::getGenre)
                .toList();

        if (preferredGenres.isEmpty()) return Collections.emptyList();

        // 2. 카테고리별 추천 후보 도서 수집 (중요 카테고리 기준)
        Set<String> seenIsbns = new HashSet<>();
        List<BookRecommendDTO> candidates = new ArrayList<>();

        for (Genre genre : preferredGenres) {
            List<Integer> topCategoryIds = genreMapperService.getTopCategoryIdsByGenre(genre); // 중요 카테고리만

            for (Integer categoryId : topCategoryIds) {
                List<BookResponse> responses = aladinService.searchBooksByCategorySafe(categoryId, 2); // 각 카테고리당 최대 2권

                for (BookResponse res : responses) {
                    if (seenIsbns.add(res.getIsbn())) {
                        candidates.add(new BookRecommendDTO(
                                res.getTitle(),
                                res.getAuthor(),
                                res.getIsbn(),
                                0.0,
                                res.getCover(),
                                res.getCategoryName(),
                                res.getCategoryId()
                        ));
                    }
                }
            }
        }

        // 3. 점수 평가 (기존 방식 유지)
        List<BookScoreDTO> scored = applyWeightedRatingToBooks(candidates);
        applyLoanScoreDTO(scored);
        applyBestsellerScore(scored);

        List<Integer> userCategoryIds = candidates.stream()
                .map(BookRecommendDTO::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        calculateTotalScoreGenre(scored);

        // 4. 상위 100권 반환
        return scored.stream()
                .sorted(Comparator.comparingDouble(BookScoreDTO::getTotalScore).reversed())
                .limit(10)
                .toList();
    }

    public List<BookScoreDTO> calculateTotalScoreGenre(List<BookScoreDTO> books) {
        for (BookScoreDTO book : books) {
            double total = 0.0;
            total += book.getWeightedRatingScore() * 0.4;
            total += book.getBestsellerScore() * 0.4;

            //total += book.getSimilarityScore() * 0.3;       // ✅ 장르 유사 점수
            total += book.getLoanScore() * 0.2;
            book.setTotalScore(total);
        }
        return books;
    }






}
