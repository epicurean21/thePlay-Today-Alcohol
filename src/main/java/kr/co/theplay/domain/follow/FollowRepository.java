package kr.co.theplay.domain.follow;

import com.sun.org.apache.xpath.internal.operations.Bool;
import kr.co.theplay.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @EntityGraph(attributePaths = {"userFollow"})
    @Query("select f from Follow f where f.user.email = :email")
    List<Follow> findFollowingsByUser(@Param("email") String email);

    Optional<Follow> findByUserAndUserFollow(User user, User userFollow);

    /*
    attributePaths가 가져오고자 하는 Entity.. userFollow인지 user인지

    나를 팔로잉 하는 사람들을 가져오는 것 이기에, user->userFollow의 user 객체들을 가져와야한다.
    Follow table의 userFollow가 로그인한 사람 (email)
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("select f from Follow f where f.userFollow.email = :email")
    List<Follow> findFollowersByUser(@Param("email") String email);


    // A가 B를 팔로잉 하고있는데 A를 가져오고싶다. A의 userId와 B의 email을 알고있음
    // A가 user, B가 userFollow인 column 을 가져와야한다.
    @Query("select f from Follow f where f.user.id = :id and f.userFollow.email = :email")
    Follow findFollowerById(@Param("email") String email, @Param("id") Long id);

    // A가 B를 팔로잉 하고있는데 이 팔로잉을 취소한다.
    @Query("select f from Follow f where f.user.email = :email and f.userFollow.id = :id")
    Follow findFollowingById(@Param("email") String email, @Param("id") Long id);

    @EntityGraph(attributePaths = {"userFollow"})
    @Query("select count (f) from Follow f where f.userFollow.email = :email")
    Long findFollowersCountByUser(@Param("email") String email);

    Boolean existsFollowByUserAndUserFollow(User user, User userFollow);
}
