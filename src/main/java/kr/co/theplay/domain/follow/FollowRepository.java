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

    /*
    attributePaths가 가져오고자 하는 Entity.. userFollow인지 user인지

    나를 팔로잉 하는 사람들을 가져오는 것 이기에, user->userFollow의 user 객체들을 가져와야한다.
    Follow table의 userFollow가 로그인한 사람 (email)
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("select f from Follow f where f.userFollow.email = :email")
    List<Follow> findFollowersByUser(@Param("email") String email);

}
