package com.example.lifetutor.hashtag.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String hashtag;

    public Hashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}

