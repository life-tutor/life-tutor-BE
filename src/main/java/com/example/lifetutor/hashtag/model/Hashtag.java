package com.example.lifetutor.hashtag.model;

import com.example.lifetutor.post.model.Post;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private String hashtag;

    public Hashtag(Post post, String hashtag) {
        this.post = post;
        this.hashtag = hashtag;
    }
}
