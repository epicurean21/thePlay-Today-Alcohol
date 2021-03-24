package kr.co.theplay.domain.post;

import kr.co.theplay.domain.BaseTimeEntity;
import kr.co.theplay.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 신고 한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String content;

    @Builder
    public Report(Long id, Post post, User user, String content) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.content = content;
    }
}
