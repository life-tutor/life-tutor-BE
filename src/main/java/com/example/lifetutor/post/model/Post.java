package com.example.lifetutor.post.model;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.utility.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Likes> likes;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String posting_content;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<PostHashtag> postHashtags;

//    @OneToMany(mappedBy = "post")
//    private List<Hashtag> hashtags;

    public Post(User user, String title, String posting_content) {
        this.user = user;
        this.title = title;
        this.posting_content = posting_content;
    }
}
