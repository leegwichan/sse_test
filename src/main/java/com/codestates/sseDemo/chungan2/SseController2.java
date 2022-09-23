package com.codestates.sseDemo.chungan2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class SseController2 {
    private static final Map<String, SseEmitter> CLIENTS = new ConcurrentHashMap<>();

    @GetMapping("/api/subscribe")
    public SseEmitter subscribe(String id) {
        SseEmitter emitter = new SseEmitter(1000000L);
        CLIENTS.put(id, emitter);
        log.info("connect id: {}", id);

        emitter.onTimeout(() -> CLIENTS.remove(id));
        emitter.onCompletion(() -> CLIENTS.remove(id));
        return emitter;
    }

    @GetMapping("/api/publish")
    public void publish(String message) {
        Set<String> deadIds = new HashSet<>();

        CLIENTS.forEach((id, emitter) -> {
            try {
                if (message.startsWith(id)){
                    emitter.send(message, MediaType.APPLICATION_JSON);
                    log.info("send message id : {}, message: {}", id, message);
                } else {
                    log.info("not send message id : {}, message: {}", id, message);
                }
            } catch (Exception e) {
                log.info("throw Exception id : {}", id);
            }
        });

        deadIds.forEach(CLIENTS::remove);
    }
}
