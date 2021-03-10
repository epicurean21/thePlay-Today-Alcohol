package kr.co.theplay.domain.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"userRole"})
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);
}
