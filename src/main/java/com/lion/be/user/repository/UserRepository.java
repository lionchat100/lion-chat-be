package com.lion.be.user.repository;

import com.lion.be.user.controller.dto.UserCardFilterRequest;
import com.lion.be.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> fetchByEmail(String email);

    User save(User user);

    Optional<User> fetchById(Long userId);

    List<User> findMatchingUsers(Long currentUserId, UserCardFilterRequest request);
}
