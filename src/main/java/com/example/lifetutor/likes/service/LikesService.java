package com.example.lifetutor.likes.service;

import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.likes.repository.LikesRepository;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
public class LikesService {

    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    @Autowired
    public LikesService(LikesRepository likesRepository, PostRepository postRepository) {
        this.likesRepository = likesRepository;
        this.postRepository = postRepository;
    }

    // 공감
    public void likes(Long postingId, User user){
        Post post = foundPost(postingId);
        if(foundLikes(post,user) != null) throw new IllegalArgumentException("이미 공감하셨습니다.");
        else likesRepository.save(new Likes(user, post));
    }

    // 공감 삭제
    public void unLikes(Long postingId, User user){
        Likes likes = foundLikes(foundPost(postingId),user);
        if(likes == null) throw new EntityNotFoundException("공감한적 없습니다.");
        else likesRepository.delete(likes);
    }

    //logic check
    public Post foundPost(Long postingId){
        return postRepository.findById(postingId).orElseThrow(
                () -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")
        );
    }
    public Likes foundLikes(Post post, User user){
        return likesRepository.findByPostAndUser(post,user);
    }
}
