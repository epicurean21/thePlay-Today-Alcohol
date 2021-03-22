package kr.co.theplay.domain.follow;

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
public class Block extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usre_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_block_id")
    private User userBlock;

    @Builder
    public Block(Long id, User user, User userBlock) {
        this.id = id;
        this.user = user;
        this.userBlock = userBlock;
    }
}
