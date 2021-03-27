package kr.co.theplay.domain.user;

import kr.co.theplay.domain.post.AlcoholTag;
import kr.co.theplay.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {
    boolean existsByAlcoholTagAndUser(AlcoholTag alcoholTag, User user);

    UserRecipe findByAlcoholTagAndUser(AlcoholTag alcoholTag, User user);

    Optional<UserRecipe> findByPostIdAndUserEmail(Long postId, String email);

    List<UserRecipe> getUserRecipeByUser(User user);
}
