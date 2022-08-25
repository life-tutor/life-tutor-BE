package com.example.lifetutor.post.model;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.utility.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public Post(User user, String title, List<Comment> comments) {
        this.user = user;
        this.title = title;
        this.comments = comments;
    }

    public Post(PostRequestDto requestDto) {
        this.user = requestDto.getUser();
        this.title = requestDto.getTitle();
        this.posting_content = requestDto.getPosting_content();
    }
}
