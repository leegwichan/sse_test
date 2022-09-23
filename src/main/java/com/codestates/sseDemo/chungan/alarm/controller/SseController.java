package com.codestates.sseDemo.chungan.alarm.controller;

import com.codestates.sseDemo.chungan.alarm.entity.Memo;
import com.codestates.sseDemo.chungan.alarm.entity.User;
import com.codestates.sseDemo.chungan.alarm.repository.MemoRepository;
import com.codestates.sseDemo.chungan.alarm.repository.UserRepository;
import com.codestates.sseDemo.chungan.alarm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@RestController
@Service
public class SseController {

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    @CrossOrigin
    @GetMapping(value = "/sub", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@RequestParam Long userId) {

        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // 연결!!
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        sseEmitters.put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> sseEmitters.remove(userId));
        sseEmitter.onError((e) -> sseEmitters.remove(userId));

        return sseEmitter;
    }

    @PostMapping("/memo/{user-id}/comment/{content}")
    public ResponseEntity addComment(
            @PathVariable("user-id") Long id,
            @PathVariable String content) {
        User findUser = userRepository.findById(id).get();
        Memo memo = new Memo(0, content, findUser);
        Memo saveMemo = memoRepository.save(memo);
        // 알림 이벤트 발행 메서드 호출
        notificationService.notifyAddCommentEvent(saveMemo.getId());

        return ResponseEntity.ok("ok");
    }

    @PostMapping("/users")
    public ResponseEntity addUser(){
        User user = new User();
        User saveUser = userRepository.save(user);

        return new ResponseEntity(user, HttpStatus.OK);
    }
}
