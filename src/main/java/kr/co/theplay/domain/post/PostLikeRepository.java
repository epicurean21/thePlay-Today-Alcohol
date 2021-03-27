package kr.co.theplay.domain.post;

import kr.co.theplay.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findByUserEmail(String email);

    @Query("select p.post from PostLike p where p.user.email = :email order by p.createdDate desc ")
    Page<Post> findPostLikeByUserEmail(Pageable pageable, @Param("email") String email);

    Optional<PostLike> findByPostIdAndUserEmail(Long id, String email);

    PostLike findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);
}
