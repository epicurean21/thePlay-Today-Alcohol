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

    @Query(value = "select distinct p from Post p " +
            "inner join PostImage pi on pi.post.id = p.id " +
            "inner join AlcoholTag at on at.post.id = p.id " +
            "order by p.createdDate desc ")
    Page<Post> getLatestPostsForMain(Pageable pageable);

    @Query(value = "select p from Post p " +
            "where p.user.email = :email " +
            "order by p.createdDate desc")
    Page<Post> getUserLastestPosts(@Param("email") String email, Pageable pageable);

    @Query(value = "select distinct p from Post p " +
            "inner join PostImage pi on pi.post.id = p.id " +
            "inner join AlcoholTag at on at.post.id = p.id " +
            "inner join Follow f on f.userFollow = p.user " +
            "where f.user.email = :email " +
            "order by p.createdDate desc ")
    Page<Post> getFollowingPosts(Pageable pageable, @Param("email") String email);

    @Query("select p from Post p where p.haveRecipeYn = 'Y' and p.user.email = :email")
    List<Post> getUserRecipePosts(@Param("email") String email);

    @Query("select distinct p from Post p " +
            "inner join PostImage pi on pi.post.id = p.id " +
            "inner join AlcoholTag at on at.post.id = p.id " +
            "inner join RecipeIngredient ri on ri.post.id = p.id " +
            "inner join RecipeStep rs on rs.post.id = p.id " +
            "where p.id = :postId ")
    Optional<Post> getRecipeByPostId(@Param("postId") Long postId);

}
