package com.example.lifetutor.likes.service;

import com.example.lifetutor.config.security.userDtail.UserDetailImpl;
import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.likes.repository.LikesRepository;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
        Post post = postNotFound(postingId);
        Likes likes = new Likes(user,post);
        //이미 공감한 상태인지 확인
        if(isLikes(post,user)) throw new IllegalArgumentException("이미 공감하셨습니다.");
        else likesRepository.save(likes);
    }

    // 공감 삭제
    public void unLikes(Long postingId, User user){
        Post post = postNotFound(postingId);
        Likes likes = foundLikes(post,user);
        if(!isLikes(post,user)) throw new IllegalArgumentException("공감한적 없습니다.");
        else likesRepository.deleteById(likes.getId());
    }

    //logic check
    public Post postNotFound(Long postingId){
        return postRepository.findById(postingId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );
    }
    public Likes foundLikes(Post post, User user){
        return likesRepository.findByPostAndUser(post,user);
    }
    public boolean isLikes(Post post, User user){
        return foundLikes(post,user)!=null;
    }
}
