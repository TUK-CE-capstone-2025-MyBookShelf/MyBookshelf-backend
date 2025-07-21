package org.wsp.mybookshelf.global.commonEntity.enums;

public enum Genre {
    GENERAL("총류"),
    LIBRARY_SCIENCE("도서학, 서지학"),
    INFORMATION_SCIENCE("문헌정보학"),
    ENCYCLOPEDIA("백과사전"),
    GENERAL_PAPERS("일반 논문집"),
    GENERAL_PERIODICALS("일반 연속간행물"),
    ASSOCIATIONS("학·협회, 기관"),
    JOURNALISM("신문, 언론, 저널리즘"),
    GENERAL_COLLECTIONS("일반 전집, 총서"),
    LOCAL_MATERIALS("향토자료"),

    PHILOSOPHY("철학"),
    METAPHYSICS("형이상학"),
    EPISTEMOLOGY("인식론, 인과론, 인간학"),
    PHILOSOPHICAL_SYSTEMS("철학의 체계"),
    CONFUCIANISM("경학"),
    EASTERN_PHILOSOPHY("동양 철학, 사상"),
    WESTERN_PHILOSOPHY("서양철학"),
    LOGIC("논리학"),
    PSYCHOLOGY("심리학"),
    ETHICS("윤리학, 도덕철학"),

    RELIGION("종교"),
    COMPARATIVE_RELIGION("비교종교학"),
    BUDDHISM("불교"),
    CHRISTIANITY("기독교"),
    TAOISM("도교"),
    CHEONDOGYO("천도교"),
    SHINTO("신도"),
    HINDUISM("바라문교, 인도교"),
    ISLAM("회교(이슬람교)"),
    OTHER_RELIGIONS("기타 제종교"),

    SOCIAL_SCIENCE("사회과학"),
    STATISTICS("통계학"),
    ECONOMICS("경제학"),
    SOCIOLOGY("사회학, 사회문제"),
    POLITICAL_SCIENCE("정치학"),
    PUBLIC_ADMINISTRATION("행정학"),
    LAW("법학"),
    EDUCATION("교육학"),
    FOLKLORE("풍속, 민속학"),
    MILITARY_SCIENCE("국방, 군사학"),

    NATURAL_SCIENCE("자연과학"),
    MATHEMATICS("수학"),
    PHYSICS("물리학"),
    CHEMISTRY("화학"),
    ASTRONOMY("천문학"),
    GEOLOGY("지학"),
    MINERALOGY("광물학"),
    BIOLOGY("생물과학"),
    BOTANY("식물학"),
    ZOOLOGY("동물학"),

    TECHNOLOGY("기술과학"),
    MEDICINE("의학"),
    AGRICULTURE("농업, 농학"),
    ENGINEERING("공학, 공업일반"),
    ARCHITECTURE("건축공학"),
    MECHANICAL_ENGINEERING("기계공학"),
    ELECTRICAL_ENGINEERING("전기공학, 전자공학"),
    CHEMICAL_ENGINEERING("화학공학"),
    MANUFACTURING("제조업"),
    HOME_ECONOMICS("가정학 및 가정생활"),

    ARTS("예술"),
    ARCHITECTURE_ART("건축술"),
    SCULPTURE("조각"),
    CRAFTS("공예, 장식미술"),
    CALLIGRAPHY("서예"),
    PAINTING("회화, 도화"),
    PHOTOGRAPHY("사진술"),
    MUSIC("음악"),
    THEATER("연극"),
    ENTERTAINMENT("오락, 운동"),

    LANGUAGE("언어"),
    KOREAN("한국어"),
    CHINESE("중국어"),
    JAPANESE("일본어"),
    ENGLISH("영어"),
    GERMAN("독일어"),
    FRENCH("프랑스어"),
    SPANISH("스페인어"),
    ITALIAN("이탈리아어"),
    OTHER_LANGUAGES("기타 제어"),

    LITERATURE("문학"),
    KOREAN_LITERATURE("한국문학"),
    CHINESE_LITERATURE("중국문학"),
    JAPANESE_LITERATURE("일본문학"),
    ENGLISH_LITERATURE("영미문학"),
    GERMAN_LITERATURE("독일문학"),
    FRENCH_LITERATURE("프랑스문학"),
    SPANISH_LITERATURE("스페인문학"),
    ITALIAN_LITERATURE("이탈리아문학"),
    OTHER_LITERATURES("기타 제문학"),

    HISTORY("역사"),
    ASIAN_HISTORY("아시아(아세아)"),
    EUROPEAN_HISTORY("유럽(구라파)"),
    AFRICAN_HISTORY("아프리카"),
    NORTH_AMERICAN_HISTORY("북아메리카(북미)"),
    OCEANIA_HISTORY("오세아니아(대양주)"),
    POLAR_REGIONS_HISTORY("양극지방"),

    GEOGRAPHY("지리"),
    BIOGRAPHY("전기"),


    UNKNOWN("알 수 없음");



    private final String description;

    Genre(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}