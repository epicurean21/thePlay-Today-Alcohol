package kr.co.theplay.domain.user;


import kr.co.theplay.domain.BaseTimeEntity;
import kr.co.theplay.domain.post.AlcoholTag;
import kr.co.theplay.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// 유저가 저장한 레시피 엔티티
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRecipe extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alcohol_tag_id")
    private AlcoholTag alcoholTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserRecipe(Long id, AlcoholTag alcoholTag, User user) {
        this.id = id;
        this.alcoholTag = alcoholTag;
        this.user = user;
    }
}
