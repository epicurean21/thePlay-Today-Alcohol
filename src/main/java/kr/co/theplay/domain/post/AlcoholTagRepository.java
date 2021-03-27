package kr.co.theplay.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlcoholTagRepository extends JpaRepository <AlcoholTag, Long> {

    List<AlcoholTag> findByPost(Post post);

    List<AlcoholTag> findByName(String tagName);

    @Query("select a from AlcoholTag a where a.post.id = :postId and a.recipeYn = 'Y'")
    AlcoholTag findByPostId(@Param("postId") Long postId);
}
