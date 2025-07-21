package org.wsp.mybookshelf.domain.community.entity;

public enum BoardType {
    FREE("자유게시판"),
    INFO("정보게시판"),
    NOTICE("공지사항");

    private final String displayName;

    BoardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
