package com.lion.be.user.repository;

import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> fetchByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> fetchById(Long userId) {
        return userJpaRepository.findById(userId);
    }

}
