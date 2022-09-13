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

    // 채팅방 조회
    @Transactional(readOnly = true)
    public RoomResponseDto getRooms(int page, int size){
        Page<Room> rooms = roomRepository.findAllByOrderByIdDesc(PageRequest.of(page,size));
        return new RoomResponseDto(rooms);
    }

    // 해쉬태그 리스트
    @Transactional(readOnly = true)
    public List<HashtagDto> searchHashtags(String keyword){
        notSearch(keyword);
        List<HashtagDto> result = new ArrayList<>();
        List<RoomHashtag> hashtags = roomHashtagRepository.findByHashtags();
        if(!keyword.isEmpty() && !hashtags.isEmpty()) result = countHashtag.roomHashtag(keyword, hashtags);
        return result;
    }

    // 채팅방 검색(기존)
    @Transactional(readOnly = true)
    public RoomResponseDto searchRooms(String hashtag,int page,int size){
        notSearch(hashtag);
        Page<Room> rooms = roomRepository.roomLikeHashtag(PageRequest.of(page,size), hashtag);
        return new RoomResponseDto(rooms);
    }

    // 채팅방 검색(최신)
    @Transactional(readOnly = true)
    public RoomResponseDto searchRoomsFinal(String hashtag,int page,int size){
        notSearch(hashtag);
        Page<Room> rooms = roomRepository.roomByHashtag(PageRequest.of(page,size), hashtag);
        return new RoomResponseDto(rooms);
    }

    // 채팅방 생성
    public ResponseEntity<Long> createRoom(RoomRequestDto requestDto, User user){
        requestDto.isEmpty();
        Room room = new Room(requestDto.getTitle(),user);
        roomRepository.save(room);
        if(!requestDto.getHashtag().isEmpty()) saveRoomHashtag(requestDto, room);
        return new ResponseEntity<>(room.getId(), HttpStatus.CREATED);
    }

    // 채팅방 수정
    public void updateRoom(Long room_id, RoomRequestDto requestDto, User user){
        Room room = foundRoom(room_id);
        // 작성자 확인
        room.validateUser(user);
        requestDto.isEmpty();
        if(room.getEnters().size() < 2) room.update(requestDto);
        else throw new IllegalArgumentException("채팅방에 게스트가 입장한 후엔 수정이 불가능합니다.");
        // 해쉬태그는 추가할 수도 있으므로 삭제 후 다시 작성
        deleteAndSaveHashtag(requestDto,room);
    }

    // 채팅방 입장
    public void enterRoom(Long room_id, User user){
        Room room = foundRoom(room_id);
        if(existUser(room,user) == null){
            if(room.getEnters().size() < 2) enterRepository.save(new Enter(user, room));
            else throw new IllegalArgumentException("인원이 다 차서 입장이 불가합니다.");
        }
    }

    // 채팅방 퇴장
    public void exitRoom(Long room_id, User user){
        Room room = roomRepository.findId(room_id).orElse(null);
        if(room != null){
            String host = room.getUser().getUsername();
            if(host.equals(user.getUsername())) deleteRoom(room_id,user);
            else enterRepository.delete(existUser(room, user));
        }
    }

    // 채팅방 삭제
    public void deleteRoom(Long room_id, User user){
        roomRepository.findById(room_id).ifPresent(room -> room.validateUser(user));
        roomRepository.deleteById(room_id);
        deleteHashtag();
    }

    // 채팅방 해쉬태그 저장
    public void saveRoomHashtag(RoomRequestDto requestDto, Room room){
        Set<String> tags = new LinkedHashSet<>();
        List<RoomHashtag> roomHashtags = new ArrayList<>();
        for(String hashtag : requestDto.getHashtag()){
            hashtag = hashtag.trim();
            if(!hashtag.isEmpty()) tags.add(hashtag);
        }
        for(String tag : tags) roomHashtags.add(saveHashtag(tag, room));
        roomHashtagRepository.saveAll(roomHashtags);
    }

    // 해쉬태그 저장
    public RoomHashtag saveHashtag(String tagStr, Room room){
        validateHashtag(tagStr);
        Hashtag tag = hashtagRepository.findByHashtag(tagStr);
        if(tag == null) tag = new Hashtag(tagStr);
        return new RoomHashtag(tag,room);
    }

    // 채팅방 해쉬태그 삭제 후 저장
    public void deleteAndSaveHashtag(RoomRequestDto requestDto, Room room){
        List<RoomHashtag> roomHashtags = roomHashtagRepository.findByRoom(room);
        if(!roomHashtags.isEmpty()){
            for(RoomHashtag roomHashtag : roomHashtags) roomHashtag.getHashtag().getRoomHashtags().remove(roomHashtag);
            roomHashtagRepository.deleteAll(roomHashtags);
            deleteHashtag();
        }
        roomHashtags.clear();
        if(!requestDto.getHashtag().isEmpty()){
            for(String tagStr : requestDto.getHashtag()) roomHashtags.add(saveHashtag(tagStr, room));
            roomHashtagRepository.saveAll(roomHashtags);
        }
    }

    // 해쉬태그 삭제
    public void deleteHashtag(){
        List<Hashtag> unused = hashtagRepository.unusedHashtags();
        if(!unused.isEmpty()) hashtagRepository.deleteAll(unused);
    }

    // 채팅방 validate
    public Room foundRoom(Long room_id){
        return roomRepository.findById(room_id).orElseThrow(
                () -> new EntityNotFoundException("방을 찾을 수 없습니다.")
        );
    }
    public void notSearch(String hashtag){
        hashtag = hashtag.trim();
        if(hashtag.isEmpty()) throw new IllegalArgumentException("검색어를 입력해주세요.");
        else validateHashtag(hashtag);
    }
    public void validateHashtag(String hashtag){
        if(hashtag.length() < 2 || hashtag.length() > 6) throw new IllegalArgumentException("2자 ~ 6자까지 입력해주세요.");
    }
    public Enter existUser(Room room, User user){
        return enterRepository.findByRoomAndUser(room,user);
    }
}
