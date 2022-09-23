package com.codestates.sseDemo.chungan.alarm.repository;

import com.codestates.sseDemo.chungan.alarm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
