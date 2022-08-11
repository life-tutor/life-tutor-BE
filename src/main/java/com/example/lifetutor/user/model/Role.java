package com.example.lifetutor.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    WORKER,
    SEEKER;

    @JsonCreator
    public static Role from(String subject) {
        return Role.valueOf(subject.toUpperCase());
    }
}
