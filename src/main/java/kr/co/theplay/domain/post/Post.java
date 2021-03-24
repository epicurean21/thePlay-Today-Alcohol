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
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String haveRecipeYn;

    @Column
    private String content;

    @Builder
    public Post(Long id, User user, String haveRecipeYn, String content){
        this.id = id;
        this.user = user;
        this.haveRecipeYn = haveRecipeYn;
        this.content = content;
    }

    public void changeUser(User user){
        this.user = user;
    }

}
