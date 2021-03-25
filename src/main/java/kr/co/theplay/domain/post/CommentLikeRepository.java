package kr.co.theplay.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 댓글 좋아요 여부
    boolean existsByPostCommentIdAndUserEmail(Long id, String email);

    // 해당 댓글 아이디를 좋아요 한 객체 수 (즉 댓글 좋아요 수)
    Long countAllByPostCommentId(Long id);
}
