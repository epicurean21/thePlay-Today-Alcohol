package kr.co.theplay.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select count(p) from Post p where p.user.email = :email")
    Long findPostCountByUser(@Param("email") String email);

    List<Post> findByUserEmail(String email);
}
