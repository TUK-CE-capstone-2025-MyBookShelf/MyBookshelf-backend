package org.wsp.mybookshelf.global.commonEntity.enums;

public enum Genre {
    FANTASY("판타지"),
    SCIENCE_FICTION("과학 소설"),
    MYSTERY("미스터리"),
    ROMANCE("로맨스"),
    THRILLER("스릴러"),
    NON_FICTION("논픽션"),
    BIOGRAPHY("전기"),
    HISTORY("역사"),
    CHILDREN("어린이");

    private final String description;

    Genre(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}