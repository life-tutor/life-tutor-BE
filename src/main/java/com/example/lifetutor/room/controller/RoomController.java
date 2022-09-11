package com.example.lifetutor.room.controller;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.room.dto.request.RoomRequestDto;
import com.example.lifetutor.room.dto.response.HashtagDto;
import com.example.lifetutor.room.dto.response.RoomResponseDto;
import com.example.lifetutor.room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // 채팅방 조회
    @GetMapping("/main/rooms")
    public RoomResponseDto getRooms(@RequestParam("page") int page, @RequestParam("size") int size){
        page = page-1;
        return roomService.getRooms(page, size);
    }

    // 해쉬태그 리스트
    @GetMapping("/hashtags/rooms")
    public List<HashtagDto> searchHashtags(@RequestParam("hashtag") String keyword){
        return roomService.searchHashtags(keyword);
    }

    // 채팅방 검색(기존)
    @GetMapping("/test/search/rooms")
    public RoomResponseDto searchRooms(@RequestParam("hashtag") String hashtag,@RequestParam("page") int page, @RequestParam("size") int size){
        page = page-1;
        return roomService.searchRooms(hashtag,page,size);
    }

    // 채팅방 검색(최신)
    @GetMapping("/search/rooms")
    public RoomResponseDto searchRoomsFinal(@RequestParam("hashtag") String hashtag,@RequestParam("page") int page, @RequestParam("size") int size){
        return roomService.searchRoomsFinal(hashtag,page,size);
    }

    // 채팅방 생성
    @PostMapping("/chat/room")
    public ResponseEntity<Long> createRoom(@RequestBody RoomRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl user){
        return roomService.createRoom(requestDto,user.getUser());
    }

    // 채팅방 수정
    @PutMapping("/chat/room/{room_id}")
    public ResponseEntity<String> updateRoom(@PathVariable Long room_id, @RequestBody RoomRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl user){
        roomService.updateRoom(room_id,requestDto,user.getUser());
        return new ResponseEntity<>("수정 성공",HttpStatus.OK);
    }

    // 채팅방 입장
    @PostMapping("/chat/room/{room_id}/enter")
    public ResponseEntity<String> enterRoom(@PathVariable Long room_id, @AuthenticationPrincipal UserDetailsImpl user){
        roomService.enterRoom(room_id, user.getUser());
        return new ResponseEntity<>(user.getUser().getNickname(),HttpStatus.OK);
    }

    // 채팅방 퇴장
    @DeleteMapping("/chat/room/{room_id}/exit")
    public ResponseEntity<String> exitRoom(@PathVariable Long room_id, @AuthenticationPrincipal UserDetailsImpl user){
        roomService.exitRoom(room_id,user.getUser());
        return new ResponseEntity<>("퇴장",HttpStatus.OK);
    }
}
