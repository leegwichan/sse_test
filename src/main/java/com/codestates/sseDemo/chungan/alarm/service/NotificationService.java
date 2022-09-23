package com.codestates.sseDemo.chungan.alarm.service;

import com.codestates.sseDemo.chungan.alarm.controller.SseController;
import com.codestates.sseDemo.chungan.alarm.entity.Memo;
import com.codestates.sseDemo.chungan.alarm.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {

    private final MemoRepository memoRepository;
    public static Map<Long, SseEmitter> sseEmitters = SseController.sseEmitters;

    public void notifyAddCommentEvent(Long memoId) {
        // 댓글에 대한 처리 후 해당 댓글이 달린 게시글의 pk값으로 게시글을 조회
        Memo memo = memoRepository.findById(memoId).orElseThrow(
                () -> new IllegalArgumentException("찾을 수 없는 메모입니다.")
        );
        Long userId = memo.getUser().getId();

        if (sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = sseEmitters.get(userId);
            try {
                sseEmitter.send(SseEmitter.event().name("addComment").data("댓글이 달렸습니다!!!!!"));
                log.info("정상 실행 되었는지?");
            } catch (Exception e) {
                sseEmitters.remove(userId);
            }
        }
    }
}
