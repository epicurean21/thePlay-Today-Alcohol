package kr.co.theplay.domain.post;

import kr.co.theplay.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("select c from PostComment c where c.post.id = :postId and c.postCommentParentId = 0")
    List<PostComment> findCommentsByPostId(@Param("postId") Long postId);

    boolean existsByPostId(Long postId);

    boolean existsByPostCommentParentId(Long commentId);

    @Query("select c from PostComment c where c.post.id = :postId and c.postCommentParentId = :postCommentId")
    List<PostComment> findSecondCommentsByCommentId(@Param("postId") Long postId, @Param("postCommentId") Long postCommentId);

    @Query("select count(pc) from PostComment pc where pc.post.id =:postId")
    Long getCountOfPostComment(@Param("postId") Long postId);

    @EntityGraph(attributePaths = "user")
    PostComment findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(Long postId, Long postCommentParentId);

    /*
    Native Query 사용 이래도 FetchType.Lazy는 어짜피 안가져옴 ㅋㅋ EntityGraph를 사용하자 ~
    @Query(value = "SELECT * FROM post_comment as p " +
            "INNER JOIN user ON p.user_id = user.id " +
            "WHERE p.post_comment_parent_id = 0 AND p.post_id = :postId " +
            "ORDER BY p.created_date DESC " +
            "limit 1", nativeQuery = true)
    PostComment findPostCommentAndUser(@Param("postId") Long postId);
    */
}
