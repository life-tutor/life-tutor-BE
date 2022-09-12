package com.example.lifetutor.room.model;

import com.example.lifetutor.hashtag.model.Hashtag;
import org.hibernate.annotations.Cache;

import javax.persistence.*;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

@Entity
@Cache(usage = READ_ONLY)
@Cacheable
public class RoomHashtag {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER,optional = false,cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name="HASHTAG_ID",nullable = false)
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="ROOM_ID",nullable = false)
    private Room room;

    public RoomHashtag(){}
    public RoomHashtag(Hashtag hashtag, Room room){
        this.hashtag = hashtag;
        if(!hashtag.getRoomHashtags().contains(this)) hashtag.getRoomHashtags().add(this);
        this.room = room;
        if(!room.getHashtags().contains(this)) room.getHashtags().add(this);
    }

    public Long getId() {
        return id;
    }

    public Hashtag getHashtag() {
        return hashtag;
    }
}
