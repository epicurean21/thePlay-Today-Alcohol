package kr.co.theplay.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select count(p) from Post p where p.user.email = :email")
    Long findPostCountByUser(@Param("email") String email);

    List<Post> findByUserEmail(String email);

    @Query(value = "select p from Post p " +
            "inner join PostImage pi on pi.post.id = p.id " +
            "inner join AlcoholTag at on at.post.id = p.id " +
            "order by p.createdDate desc ")
    Page<Post> getLatestPostsForMain(Pageable pageable);
}
