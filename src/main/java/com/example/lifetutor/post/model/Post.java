package com.example.lifetutor.post.model;

import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.utility.Timestamped;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private List<Likes> postLikes = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    public List<Likes> getLikes() {
        return postLikes;
    }
}
