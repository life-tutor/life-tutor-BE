package com.example.lifetutor.room.service;

import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.room.dto.request.RoomRequestDto;
import com.example.lifetutor.room.dto.response.RoomResponseDto;
import com.example.lifetutor.room.model.Enter;
import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.room.model.RoomHashtag;
import com.example.lifetutor.room.repository.EnterRepository;
import com.example.lifetutor.room.repository.RoomHashtagRepository;
import com.example.lifetutor.room.repository.RoomRepository;
import com.example.lifetutor.room.dto.response.ContentResponseDto;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
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
    public RoomResponseDto getRooms(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Room> rooms = roomRepository.findAllByOrderByIdDesc(pageable);
        return roomResponse(rooms);
    }

    // 채팅방 검색
    public RoomResponseDto searchRooms(String hashtag,int page,int size){
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
        Enter enter = new Enter(user,room);
        enterRepository.save(enter);
        if(!requestDto.getHashtag().isEmpty()){
            Set<String> tags = new LinkedHashSet<>();
            for(String hashtag : requestDto.getHashtag()){
                hashtag = hashtag.trim();
                if(!hashtag.isEmpty()) tags.add(hashtag);
            }
            for(String tag : tags){
                saveHashtag(tag,room);
            }
        }
        return new ResponseEntity<>(room.getId().toString(), HttpStatus.CREATED);
    }

    // 채팅방 수정
    public void updateRoom(Long room_id, RoomRequestDto requestDto, User user){
        Room room = foundRoom(room_id);
        // 작성자 확인
        room.validateUser(user);
        isEmpty(requestDto);
        if(room.getEnters().size() == 1) room.update(requestDto);
        else throw new IllegalArgumentException("채팅방에 게스트가 입장한 후엔 수정이 불가능합니다.");
        // 해쉬태그는 추가할 수도 있으므로 삭제 후 다시 작성
        if(!room.getHashtags().isEmpty()){
            for(RoomHashtag roomHashtag : room.getHashtags()){
                roomHashtagRepository.deleteById(roomHashtag.getId());
            }
        }
        if(!requestDto.getHashtag().isEmpty()){
            for(String tagStr : requestDto.getHashtag()){
                saveHashtag(tagStr,room);
            }
        }
    }

    // 채팅방 입장
    public void enterRoom(Long room_id, User user){
        Room room = foundRoom(room_id);
        List<Enter> enters = room.getEnters();
        if (enters.size() < 2) checkHost(room, user, "enter");
        else throw new IllegalArgumentException("인원이 다 차서 입장이 불가합니다.");
    }

    // 채팅방 퇴장
    public void exitRoom(Long room_id, User user){
        Room room = foundRoom(room_id);
        String host = room.getUser().getUsername();
        checkHost(room,user,"exit");
        if(host.equals(user.getUsername())) deleteRoom(room_id,user);
    }

    // 채팅방 삭제
    public void deleteRoom(Long room_id, User user){
        roomRepository.findById(room_id).ifPresent(room -> room.validateUser(user));
        roomRepository.deleteById(room_id);
    }

    // 채팅방 조회  response
    public RoomResponseDto roomResponse(Page<Room> rooms){
        boolean isLast = rooms.isLast();
        List<ContentResponseDto> content = new ArrayList<>();
        for(Room room : rooms){
            List<String> tags = new ArrayList<>();
            for(RoomHashtag roomHashtag : room.getHashtags()){
                tags.add(roomHashtag.getHashtag().getHashtag());
            }
            ContentResponseDto responseDto = new ContentResponseDto(room,tags);
            content.add(responseDto);
        }
//        if(content.isEmpty()) throw new IllegalArgumentException("채팅방이 없습니다.");
        return new RoomResponseDto(isLast,content);
    }

    // 해쉬태그 저장
    public void saveHashtag(String tagStr, Room room){
        Hashtag tag = hashtagRepository.findByHashtag(tagStr);
        if(tag == null){
            Hashtag hashtag = new Hashtag(tagStr);
            RoomHashtag roomHashtag = new RoomHashtag(hashtag,room);
            roomHashtagRepository.save(roomHashtag);
        }else{
            RoomHashtag roomHashtag = new RoomHashtag(tag,room);
            roomHashtagRepository.save(roomHashtag);
        }
    }

    // 채팅방 validate
    public Room foundRoom(Long room_id){
        return roomRepository.findById(room_id).orElseThrow(
                () -> new IllegalArgumentException("방을 찾을 수 없습니다.")
        );
    }
    public void isEmpty(RoomRequestDto requestDto){
        if(requestDto.getTitle().isEmpty()) throw new IllegalArgumentException("제목을 입력해주세요.");
    }
    public void notSearch(String hashtag){
        if(hashtag.isEmpty()) throw new IllegalArgumentException("검색어를 입력해주세요.");
    }
    // 채팅방 host 확인
    public void checkHost(Room room, User user, String check){
        String host = room.getUser().getUsername();
        String guest = user.getUsername();
        if(!host.equals(guest)){
            if(check.equals("enter")){
                Enter newEnter = new Enter(user,room);
                enterRepository.save(newEnter);
            }
            if(check.equals("exit")){
                Enter exitUser = enterRepository.findByUser(user);
                enterRepository.delete(exitUser);
            }
        }
    }
}
