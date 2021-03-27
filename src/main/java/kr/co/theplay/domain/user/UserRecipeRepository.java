package kr.co.theplay.domain.user;

import kr.co.theplay.domain.post.AlcoholTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {
    boolean existsByAlcoholTagAndUser(AlcoholTag alcoholTag, User user);

    UserRecipe findByAlcoholTagAndUser(AlcoholTag alcoholTag, User user);

    Optional<UserRecipe> findByPostIdAndUserEmail(Long postId, String email);

    List<UserRecipe> getUserRecipeByUser(User user);

    Page<UserRecipe> findByUserId(Pageable pageable, Long userId);
}
