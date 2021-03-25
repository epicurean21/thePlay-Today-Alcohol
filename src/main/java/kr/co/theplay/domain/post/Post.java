package kr.co.theplay.domain.post;

import kr.co.theplay.domain.BaseTimeEntity;
import kr.co.theplay.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "post")
    private List<PostImage> images;

    @OneToMany(mappedBy = "post")
    private List<AlcoholTag> alcoholTags;

    @OneToMany(mappedBy = "post")
    private List<RecipeIngredient> ingredients;

    @OneToMany(mappedBy = "post")
    private List<RecipeStep> steps;

    @Builder
    public Post(Long id, User user, String haveRecipeYn, String content, List<PostImage> images,
                List<AlcoholTag> alcoholTags, List<RecipeIngredient> ingredients, List<RecipeStep> steps){
        this.id = id;
        this.user = user;
        this.haveRecipeYn = haveRecipeYn;
        this.content = content;
        this.images = images;
        this.alcoholTags = alcoholTags;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public void changeUser(User user){
        this.user = user;
    }

    public void updatePost(String content, String haveRecipeYn){
        this.content = content;
        this.haveRecipeYn = haveRecipeYn;
    }

}
