package com.codestates.sseDemo.chungan.alarm.repository;

import com.codestates.sseDemo.chungan.alarm.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {
}
