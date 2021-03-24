package kr.co.theplay.domain.post;

import kr.co.theplay.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    Optional<PostReport> findByUserAndPost(User user, Post post);

    @Query("select count(p) from PostReport p where p.post.user.email = :email")
    Integer findCountReportByUser(@Param("email") String email);

}
