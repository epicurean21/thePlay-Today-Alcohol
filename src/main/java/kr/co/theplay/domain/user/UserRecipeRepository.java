package kr.co.theplay.domain.user;

import kr.co.theplay.domain.post.AlcoholTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {
    boolean existsByAlcoholTagAndUser(AlcoholTag alcoholTag, User user);

    UserRecipe findByAlcoholTagAndUser(AlcoholTag alcoholTag, User user);

    Optional<UserRecipe> findByPostIdAndUserEmail(Long postId, String email);

    List<UserRecipe> getUserRecipeByUser(User user);

    Page<UserRecipe> findByUserIdOrderByCreatedDateDesc(Pageable pageable, Long userId);

    @Query("SELECT ur FROM UserRecipe ur " +
            "WHERE ur.user.email =:email " +
            "and ur.alcoholTag.name =:recipeName " +
            "ORDER BY ur.createdDate desc ")
    List<UserRecipe> findByKeyword(@Param("email") String email, @Param("recipeName") String name);
}
