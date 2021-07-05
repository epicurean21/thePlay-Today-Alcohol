package kr.co.theplay.domain.follow;

import kr.co.theplay.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByUserAndUserBlock(User user, User userBlock);

    @Query("select b from Block b where b.user.email = :email")
    List<Block> findAllByEmail(@Param("email") String email);
}
