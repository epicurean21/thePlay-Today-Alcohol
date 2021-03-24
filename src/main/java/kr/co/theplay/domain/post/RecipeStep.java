package kr.co.theplay.domain.post;

import kr.co.theplay.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeStep extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    private Integer number;

    @Column
    private String content;

    @Builder
    public RecipeStep(Long id, Post post, Integer number, String content){
        this.id = id;
        this.post = post;
        this.number = number;
        this.content = content;
    }

    public void changePost(Post post){
        this.post = post;
    }
}
