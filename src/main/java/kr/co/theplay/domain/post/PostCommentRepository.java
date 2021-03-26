package kr.co.theplay.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("select c from PostComment c where c.post.id = :postId and c.postCommentParentId = 0")
    List<PostComment> findCommentsByPostId(@Param("postId") Long postId);

    boolean existsByPostId(Long postId);

    boolean existsByPostCommentParentId(Long commentId);

    @Query("select c from PostComment c where c.post.id = :postId and c.postCommentParentId = :postCommentId")
    List<PostComment> findSecondCommentsByCommentId(@Param("postId") Long postId, @Param("postCommentId") Long postCommentId);

    @Query("select count(pc) from PostComment pc where pc.post.id =:postId")
    Long getCountOfPostComment(@Param("postId") Long postId);

    PostComment findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(Long postId, Long postCommentParentId);

}
