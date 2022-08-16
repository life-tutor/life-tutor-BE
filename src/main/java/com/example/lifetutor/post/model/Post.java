package com.example.lifetutor.post.model;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.utility.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Likes> postLikes;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String posting_content;

//    @Column(nullable = false)
//    private boolean isLike;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    public Post(User user, String title, String posting_content) {
        this.user = user;
        this.title = title;
        this.posting_content = posting_content;
    }

    public List<Likes> getLikes() {
        return postLikes;
    }

    public List<Comment> getComments() {
        return comments;
    }


}
