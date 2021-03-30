package kr.co.theplay.domain.post;

import kr.co.theplay.common.IconKind;
import kr.co.theplay.common.IconKindConverter;
import kr.co.theplay.domain.BaseTimeEntity;
import kr.co.theplay.domain.user.UserRecipe;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "alcoholTag", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserRecipe> userRecipes = new ArrayList<>();

    @Builder
    public AlcoholTag(Long id, Post post, IconKind iconKind,
                      String name, Integer color, String recipeYn, List<UserRecipe> userRecipes){
        this.id = id;
        this.post = post;
        this.iconKind = iconKind;
        this.name = name;
        this.color = color;
        this.recipeYn = recipeYn;
        this.userRecipes = userRecipes;
    }

    public void changePost(Post post){
        this.post = post;
    }

}
