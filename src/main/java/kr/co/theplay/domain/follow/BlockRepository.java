package kr.co.theplay.domain.follow;

import kr.co.theplay.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByUserAndUserBlock(User user, User userBlock);
}
