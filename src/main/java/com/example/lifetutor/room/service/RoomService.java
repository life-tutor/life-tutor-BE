package com.example.lifetutor.room.service;

import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.room.dto.request.RoomRequestDto;
import com.example.lifetutor.room.dto.response.ContentResponseDto;
import com.example.lifetutor.room.dto.response.HashtagDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomHashtagRepository roomHashtagRepository;
    private final HashtagRepository hashtagRepository;
    private final EnterRepository enterRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, RoomHashtagRepository roomHashtagRepository, HashtagRepository hashtagRepository, EnterRepository enterRepository) {
        this.roomRepository = roomRepository;
        this.roomHashtagRepository = roomHashtagRepository;
        this.hashtagRepository = hashtagRepository;
        this.enterRepository = enterRepository;
    }

    // 채팅방 조회
    @Transactional(readOnly = true)
    public RoomResponseDto getRooms(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Room> rooms = roomRepository.findAllByOrderByIdDesc(pageable);
        return roomResponse(rooms);
    }

    // 해쉬태그 리스트
    @Transactional(readOnly = true)
    public List<HashtagDto> searchHashtags(String keyword,int page,int size){
        notSearch(keyword);
        Pageable pageable = PageRequest.of(page,size);
        List<HashtagDto> result = new ArrayList<>();
        Page<RoomHashtag> hashtags = roomHashtagRepository.findByHashtags(pageable);
        if(!keyword.isEmpty() && !hashtags.isEmpty()) result = countHashtag(keyword, hashtags.getContent());
        return result;
    }

    // 채팅방 검색(기존)
    @Transactional(readOnly = true)
    public RoomResponseDto searchRooms(String hashtag,int page,int size){
        notSearch(hashtag);
        Pageable pageable = PageRequest.of(page,size);
        Page<Room> rooms = roomRepository.roomLikeHashtag(pageable, hashtag);
        return roomResponse(rooms);
    }

    // 채팅방 검색(최신)
    @Transactional(readOnly = true)
    public RoomResponseDto searchRoomsFinal(String hashtag,int page,int size){
        notSearch(hashtag);
        Pageable pageable = PageRequest.of(page,size);
        Page<Room> rooms = roomRepository.roomByHashtag(pageable, hashtag);
        return roomResponse(rooms);
    }

    // 채팅방 생성
    public ResponseEntity<String> createRoom(RoomRequestDto requestDto, User user){
        isEmpty(requestDto);
        Room room = new Room(requestDto,user);
        roomRepository.save(room);
        if(!requestDto.getHashtag().isEmpty()){
            Set<String> tags = new LinkedHashSet<>();
            List<RoomHashtag> roomHashtags = new ArrayList<>();
            for(String hashtag : requestDto.getHashtag()){
                hashtag = hashtag.trim();
                if(!hashtag.isEmpty()) tags.add(hashtag);
            }
            for(String tag : tags){
                RoomHashtag roomHashtag = saveHashtag(tag,room);
                roomHashtags.add(roomHashtag);
            }
            roomHashtagRepository.saveAll(roomHashtags);
        }
        return new ResponseEntity<>(room.getId().toString(), HttpStatus.CREATED);
    }

    // 채팅방 수정
    public void updateRoom(Long room_id, RoomRequestDto requestDto, User user){
        Room room = foundRoom(room_id);
        // 작성자 확인
        room.validateUser(user);
        isEmpty(requestDto);
        if(room.getEnters().size() < 2) room.update(requestDto);
        else throw new IllegalArgumentException("채팅방에 게스트가 입장한 후엔 수정이 불가능합니다.");
        // 해쉬태그는 추가할 수도 있으므로 삭제 후 다시 작성
        List<RoomHashtag> roomHashtags = roomHashtagRepository.findByRoom(room);
        if(!roomHashtags.isEmpty()){
            for(RoomHashtag roomHashtag : roomHashtags){
                roomHashtag.getHashtag().getRoomHashtags().remove(roomHashtag);
            }
            roomHashtagRepository.deleteAll(roomHashtags);
            deleteHashtag();
        }
        roomHashtags.clear();
        if(!requestDto.getHashtag().isEmpty()){
//            List<RoomHashtag> roomHashtags = new ArrayList<>();
            for(String tagStr : requestDto.getHashtag()){
                RoomHashtag roomHashtag = saveHashtag(tagStr,room);
                roomHashtags.add(roomHashtag);
            }
            roomHashtagRepository.saveAll(roomHashtags);
        }
    }

    // 채팅방 입장
    public void enterRoom(Long room_id, User user){
        Room room = foundRoom(room_id);
        if(room.getEnters().size() < 2){
            Enter enter = new Enter(user,room);
            enterRepository.save(enter);
        }else throw new IllegalArgumentException("인원이 다 차서 입장이 불가합니다.");
    }

    // 채팅방 퇴장
    public void exitRoom(Long room_id, User user){
//        Room room = foundRoom(room_id);
        Room room = roomRepository.findById(room_id).orElse(null);
        if(room != null){
            String host = room.getUser().getUsername();
            if(host.equals(user.getUsername())) deleteRoom(room_id,user);
            else{
                Enter exitUser = enterRepository.findByRoomAndUser(room,user);
                enterRepository.delete(exitUser);
            }
        }
    }

    // 채팅방 삭제
    public void deleteRoom(Long room_id, User user){
        roomRepository.findById(room_id).ifPresent(room -> room.validateUser(user));
        roomRepository.deleteById(room_id);
        deleteHashtag();
    }

    // 채팅방 조회  response
    public RoomResponseDto roomResponse(Page<Room> rooms){
        boolean isLast = rooms.isLast();
        List<ContentResponseDto> content = new ArrayList<>();
        for(Room room : rooms){
            List<String> tags = new ArrayList<>();
            List<RoomHashtag> roomHashtags = roomHashtagRepository.findByRoom(room);
            for(RoomHashtag roomHashtag : roomHashtags){
                tags.add(roomHashtag.getHashtag().getHashtag());
            }
            ContentResponseDto responseDto = new ContentResponseDto(room,tags);
            content.add(responseDto);
        }
//        if(content.isEmpty()) throw new IllegalArgumentException("채팅방이 없습니다.");
        return new RoomResponseDto(isLast,content);
    }

    // 해쉬태그 저장
    public RoomHashtag saveHashtag(String tagStr, Room room){
        validateHashtag(tagStr);
        Hashtag tag = hashtagRepository.findByHashtag(tagStr);
        if(tag == null) tag = new Hashtag(tagStr);
        return new RoomHashtag(tag,room);
//        roomHashtagRepository.save(roomHashtag);
    }

    // 해쉬태그 삭제
    public void deleteHashtag(){
        List<Hashtag> unused = hashtagRepository.unusedHashtags();
        if(!unused.isEmpty()) hashtagRepository.deleteAll(unused);
    }

    // 해쉬태그 중복 count
    public List<HashtagDto> countHashtag(String keyword, List<RoomHashtag> hashtags){
        Map<String,Integer> map = new LinkedHashMap<>();
        for(RoomHashtag roomHashtag : hashtags){
            String hashtag = roomHashtag.getHashtag().getHashtag();
            if(hashtag.contains(keyword)) map.put(hashtag, map.getOrDefault(hashtag, 0) + 1);
        }
        return sortedHashtag(map);
    }

    // 해쉬태그 정렬
    public List<HashtagDto> sortedHashtag(Map<String,Integer> map){
        List<HashtagDto> result = new ArrayList<>();
        List<Map.Entry<String,Integer>> entries = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList());
        for(Map.Entry<String,Integer> entry : entries){
            HashtagDto hashtagDto = new HashtagDto(entry.getKey(),entry.getValue());
            result.add(hashtagDto);
        }
        return result;
    }

    // 채팅방 validate
    public Room foundRoom(Long room_id){
        return roomRepository.findById(room_id).orElseThrow(
                () -> new IllegalArgumentException("방을 찾을 수 없습니다.")
        );
    }
    public void isEmpty(RoomRequestDto requestDto){
        String title = requestDto.getTitle();
        title = title.trim();
        if(title.isEmpty()) throw new IllegalArgumentException("제목을 입력해주세요.");
    }
    public void notSearch(String hashtag){
        hashtag.trim();
        if(hashtag.isEmpty()) throw new IllegalArgumentException("검색어를 입력해주세요.");
        else validateHashtag(hashtag);
    }
    public void validateHashtag(String hashtag){
        if(hashtag.length() < 2 || hashtag.length() > 6) throw new IllegalArgumentException("2자 ~ 6자까지 입력해주세요.");
    }
}
