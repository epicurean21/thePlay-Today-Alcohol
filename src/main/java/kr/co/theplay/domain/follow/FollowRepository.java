package kr.co.theplay.domain.follow;

import kr.co.theplay.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import javax.persistence.Entity;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @EntityGraph(attributePaths = {"userFollow"})
    @Query("select f from Follow f where f.user.email = :email")
    List<Follow> findFollowingsByUser(@Param("email") String email);

    Optional<Follow> findByUserAndUserFollow(User user, User userFollow);

}
