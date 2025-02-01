package org.wsp.mybookshelf.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);

    // 닉네임으로 사용자 찾기
    Optional<User> findByNickName(String nickname);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 닉네임 중복 확인
    boolean existsByNickName(String nickname);
    
    //이메일, 생년월일로 회원찾기
    Optional<User> findByEmailAndBirthDate(String email, LocalDate birthDate);

}
