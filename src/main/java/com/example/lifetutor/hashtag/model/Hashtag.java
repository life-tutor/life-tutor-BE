package com.example.lifetutor.hashtag.model;

import com.example.lifetutor.room.model.RoomHashtag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String hashtag;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<RoomHashtag> roomHashtags = new ArrayList<>();

    public Hashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public List<RoomHashtag> getRoomHashtags() {
        return roomHashtags;
    }
}

