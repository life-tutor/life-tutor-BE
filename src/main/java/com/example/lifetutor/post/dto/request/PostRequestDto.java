package com.example.lifetutor.post.dto.request;

import com.example.lifetutor.user.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostRequestDto{

    private User user;
    private String title;
    private String posting_content;
    private List<String> hashtag;
}
