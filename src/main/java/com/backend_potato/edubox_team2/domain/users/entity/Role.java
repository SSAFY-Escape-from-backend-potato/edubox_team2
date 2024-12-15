package com.backend_potato.edubox_team2.domain.users.entity;

public enum Role {
    USER("일반 사용자"),
    CREATOR("판매자"),
    ADMIN("관리자");

    private final String description;

    private Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
