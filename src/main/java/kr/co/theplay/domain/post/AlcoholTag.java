package kr.co.theplay.domain.post;

import kr.co.theplay.common.IconKind;
import kr.co.theplay.common.IconKindConverter;
import kr.co.theplay.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlcoholTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    @Convert(converter = IconKindConverter.class)
    private IconKind iconKind;

    @Column
    private String name;

    @Column
    private Integer color;

    @Column
    private String recipeYn;

    @Builder
    public AlcoholTag(Long id, Post post, IconKind iconKind,
                      String name, Integer color, String recipeYn){
        this.id = id;
        this.post = post;
        this.iconKind = iconKind;
        this.name = name;
        this.color = color;
        this.recipeYn = recipeYn;
    }

    public void changePost(Post post){
        this.post = post;
    }

}
