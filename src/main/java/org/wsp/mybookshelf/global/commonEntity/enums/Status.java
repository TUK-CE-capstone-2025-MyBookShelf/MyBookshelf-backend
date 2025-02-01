
package org.wsp.mybookshelf.global.commonEntity.enums;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("활성"),
    UNACTIVE("비활성");

    private final String description; // 상태 설명 필드

    // 생성자
    Status(String description) {
        this.description = description;
    }
}
