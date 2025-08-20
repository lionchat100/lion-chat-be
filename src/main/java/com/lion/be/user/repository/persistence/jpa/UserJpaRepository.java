package com.lion.be.user.repository.persistence.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lion.be.user.domain.entity.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

	boolean existsByNickname(String nickname);

    List<User> findByIdIn(Collection<Long> ids);

	@Query("SELECT u.nickname FROM User u WHERE u.id = :id")
	Optional<String> findNicknameById(@Param("id") Long userId);

	@Query("""
select u from User u
where u.id in :userIds
"""
	)
	List<User> fetchAllUser(@Param("userIds") List<Long> userIds);
}
