package kr.co.theplay.domain.post;

import kr.co.theplay.domain.BaseTimeEntity;
import kr.co.theplay.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostComment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "postComment", cascade = CascadeType.REMOVE)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @Column
    private Long postCommentParentId;

    @Column
    private String content;

    @Builder
    public PostComment(Long id, Post post, User user, List<CommentLike> commentLikes,
                       Long postCommentParentId, String content) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.commentLikes = commentLikes;
        this.postCommentParentId = postCommentParentId;
        this.content = content;
    }
}
