package kr.co.theplay.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    @Query("select r from RecipeIngredient r where r.post.user.email = :email")
    List<RecipeIngredient> findByUserEmail(@Param("email") String email);

    @Query(value = "select ri from RecipeIngredient ri where ri.post.id = :postId")
    List<RecipeIngredient> findByPostId(@Param("postId") Long postId);
}
