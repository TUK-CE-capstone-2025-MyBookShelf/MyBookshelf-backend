package org.wsp.mybookshelf.global.commonEntity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String description; // 성별 설명

    @Override
    public String toString() {
        return description; // 설명을 문자열로 출력
    }
}