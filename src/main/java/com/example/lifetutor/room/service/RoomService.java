package com.example.lifetutor.room.service;

import com.example.lifetutor.hashtag.model.CountHashtag;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.room.dto.request.RoomRequestDto;
import com.example.lifetutor.hashtag.dto.response.HashtagDto;
import com.example.lifetutor.room.dto.response.RoomResponseDto;
import com.example.lifetutor.room.model.Enter;
import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.room.model.RoomHashtag;
import com.example.lifetutor.room.repository.EnterRepository;
import com.example.lifetutor.room.repository.RoomHashtagRepository;
import com.example.lifetutor.room.repository.RoomRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomHashtagRepository roomHashtagRepository;
    private final HashtagRepository hashtagRepository;
    private final EnterRepository enterRepository;
    private final CountHashtag countHashtag;

    @Autowired
    public RoomService(RoomRepository roomRepository, RoomHashtagRepository roomHashtagRepository, HashtagRepository hashtagRepository, EnterRepository enterRepository, CountHashtag countHashtag) {
        this.roomRepository = roomRepository;
        this.roomHashtagRepository = roomHashtagRepository;
        this.hashtagRepository = hashtagRepository;
        this.enterRepository = enterRepository;
        this.countHashtag = countHashtag;
    }

    // ????????? ??????
    @Transactional(readOnly = true)
    public RoomResponseDto getRooms(int page, int size){
        Page<Room> rooms = roomRepository.findAllByOrderByIdDesc(PageRequest.of(page,size));
        return new RoomResponseDto(rooms);
    }

    // ???????????? ?????????
    @Transactional(readOnly = true)
    public List<HashtagDto> searchHashtags(String keyword){
        notSearch(keyword);
        List<HashtagDto> result = new ArrayList<>();
        List<RoomHashtag> hashtags = roomHashtagRepository.findByHashtags();
        if(!keyword.isEmpty() && !hashtags.isEmpty()) result = countHashtag.roomHashtag(keyword, hashtags);
        return result;
    }

    // ????????? ??????(??????)
    @Transactional(readOnly = true)
    public RoomResponseDto searchRooms(String hashtag,int page,int size){
        notSearch(hashtag);
        Page<Room> rooms = roomRepository.roomLikeHashtag(PageRequest.of(page,size), hashtag);
        return new RoomResponseDto(rooms);
    }

    // ????????? ??????(??????)
    @Transactional(readOnly = true)
    public RoomResponseDto searchRoomsFinal(String hashtag,int page,int size){
        notSearch(hashtag);
        Page<Room> rooms = roomRepository.roomByHashtag(PageRequest.of(page,size), hashtag);
        return new RoomResponseDto(rooms);
    }

    // ????????? ??????
    public ResponseEntity<Long> createRoom(RoomRequestDto requestDto, User user){
        Room room = new Room(requestDto.getTitle(),user);
        roomRepository.save(room);
        if(!requestDto.getHashtag().isEmpty()) saveRoomHashtag(requestDto, room);
        return new ResponseEntity<>(room.getId(), HttpStatus.CREATED);
    }

    // ????????? ??????
    public void updateRoom(Long room_id, RoomRequestDto requestDto, User user){
        Room room = foundRoom(room_id);
        // ????????? ??????
        room.validateUser(user);
        if(room.getEnters().size() < 2) room.update(requestDto);
        else throw new IllegalArgumentException("???????????? ???????????? ????????? ?????? ????????? ??????????????????.");
        // ??????????????? ????????? ?????? ???????????? ?????? ??? ?????? ??????
        deleteAndSaveHashtag(requestDto,room);
    }

    // ????????? ??????
    public void enterRoom(Long room_id, User user){
        Room room = foundRoom(room_id);
        if(existUser(room,user) == null){
            if(room.getEnters().size() < 2) enterRepository.save(new Enter(user, room));
            else throw new IllegalArgumentException("????????? ??? ?????? ????????? ???????????????.");
        }
    }

    // ????????? ??????
    public void exitRoom(Long room_id, User user){
        Room room = roomRepository.findId(room_id).orElse(null);
        if(room != null){
            String host = room.getUser().getUsername();
            if(host.equals(user.getUsername())) deleteRoom(room_id,user);
            else enterRepository.delete(existUser(room, user));
        }
    }

    // ????????? ??????
    public void deleteRoom(Long room_id, User user){
        roomRepository.findById(room_id).ifPresent(room -> room.validateUser(user));
        roomRepository.deleteById(room_id);
        deleteHashtag();
    }

    // ????????? ???????????? ??????
    public void saveRoomHashtag(RoomRequestDto requestDto, Room room){
        Set<String> tags = new LinkedHashSet<>();
        List<RoomHashtag> roomHashtags = new ArrayList<>();
        for(String hashtag : requestDto.getHashtag()){
            hashtag = hashtag.trim();
            validateHashtag(hashtag);
            if(!hashtag.isEmpty()) tags.add(hashtag);
        }
        for(String tag : tags) roomHashtags.add(foundHashtag(tag, room));
        roomHashtagRepository.saveAll(roomHashtags);
    }

    // ????????? ???????????? ?????? ??? ??????
    public void deleteAndSaveHashtag(RoomRequestDto requestDto, Room room){
        List<RoomHashtag> roomHashtags = roomHashtagRepository.findByRoom(room);
        if(!roomHashtags.isEmpty()){
            for(RoomHashtag roomHashtag : roomHashtags) roomHashtag.getHashtag().getRoomHashtags().remove(roomHashtag);
            roomHashtagRepository.deleteAll(roomHashtags);
            deleteHashtag();
        }
        roomHashtags.clear();
        if(!requestDto.getHashtag().isEmpty()){
            for(String tagStr : requestDto.getHashtag()) roomHashtags.add(foundHashtag(tagStr, room));
            roomHashtagRepository.saveAll(roomHashtags);
        }
    }

    // ???????????? ??????
    public RoomHashtag foundHashtag(String tagStr, Room room){
        Hashtag tag = hashtagRepository.findByHashtag(tagStr);
        if(tag == null) tag = new Hashtag(tagStr);
        return new RoomHashtag(tag,room);
    }

    // ???????????? ??????
    public void deleteHashtag(){
        List<Hashtag> unused = hashtagRepository.unusedHashtags();
        if(!unused.isEmpty()) hashtagRepository.deleteAll(unused);
    }

    // ????????? validate
    public Room foundRoom(Long room_id){
        return roomRepository.findById(room_id).orElseThrow(
                () -> new EntityNotFoundException("?????? ?????? ??? ????????????.")
        );
    }
    public void notSearch(String hashtag){
        hashtag = hashtag.trim();
        if(hashtag.isEmpty()) throw new IllegalArgumentException("???????????? ??????????????????.");
        else validateHashtag(hashtag);
    }
    public void validateHashtag(String hashtag){
        if(hashtag.length() < 2 || hashtag.length() > 6) throw new IllegalArgumentException("2??? ~ 6????????? ??????????????????.");
    }
    public Enter existUser(Room room, User user){
        return enterRepository.findByRoomAndUser(room,user);
    }
}
