package kr.co.theplay.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    @Query(value = "select rs from RecipeStep rs where rs.post.id = :postId")
    List<RecipeStep> findByPostId(@Param("postId") Long postId);
}
