package com.example.lifetutor.likes.service;

import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.likes.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {

    private final LikesRepository likesRepository;
    private final PostRepository postRepository;

    // 공감
    public ResponseEntity<String> getLikes(Long postingId, UserDetailImpl userDetail){
        Post post = postRepository.findById(postingId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );
        Likes likes = new Likes();
        // 연관관계 편의 메소드
        likes.setPost(post);
        likes.setUser(userDetail);

        likesRepository.save(likes);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 공감 삭제
    public ResponseEntity<String> deleteLikes(Long postingId, UserDetailImpl userDetail){
        Post post = postRepository.findById(postingId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );
        Likes likes = likesRepository.findByPostAndUser(post,userDetail.getUser());
        if (likes != null){
            Long likesId = likes.getId();
            likesRepository.deleteById(likesId);
        } else throw new IllegalArgumentException("공감한 게 없습니다.");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
