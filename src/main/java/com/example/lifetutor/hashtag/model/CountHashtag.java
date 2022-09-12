package com.example.lifetutor.hashtag.model;

import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.hashtag.dto.response.HashtagDto;
import com.example.lifetutor.room.model.RoomHashtag;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CountHashtag {

    // 채팅방 해쉬태그 중복 count
    public List<HashtagDto> roomHashtag(String keyword, List<RoomHashtag> hashtags){
        Map<String,Integer> map = new LinkedHashMap<>();
        for(RoomHashtag roomHashtag : hashtags){
            String hashtag = roomHashtag.getHashtag().getHashtag();
            if(hashtag.contains(keyword)) map.put(hashtag, map.getOrDefault(hashtag, 0) + 1);
        }
        return sortedHashtag(map);
    }

    // 게시판 해쉬태그 중복 count
    public List<HashtagDto> postHashtag(String keyword, List<PostHashtag> hashtags){
        Map<String,Integer> map = new LinkedHashMap<>();
        for(PostHashtag postHashtag : hashtags){
            String hashtag = postHashtag.getHashtag().getHashtag();
            if(hashtag.contains(keyword)) map.put(hashtag, map.getOrDefault(hashtag, 0) + 1);
        }
        return sortedHashtag(map);
    }

    // 해쉬태그 정렬
    public List<HashtagDto> sortedHashtag(Map<String, Integer> map) {
        List<HashtagDto> result = new ArrayList<>();
        List<Map.Entry<String, Integer>> entries = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList());
        for (Map.Entry<String, Integer> entry : entries) result.add(new HashtagDto(entry.getKey(), entry.getValue()));
        return result;
    }
}
