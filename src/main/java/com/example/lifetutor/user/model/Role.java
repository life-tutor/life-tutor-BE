package com.example.lifetutor.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    SEEKER,
    JUNIOR,
    SENIOR;

    @JsonCreator
    public static Role from(String subject) {
        return Role.valueOf(subject.toUpperCase());
    }
}
