package kr.co.theplay.domain.post;

import kr.co.theplay.domain.BaseTimeEntity;
import kr.co.theplay.domain.user.User;
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<AlcoholTag> alcoholTags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<RecipeStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostComment> postComments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostReport> postReports = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<UserRecipe> userRecipes = new ArrayList<>();

    @Builder
    public Post(Long id, User user, String haveRecipeYn, String content, List<PostImage> images,
                List<AlcoholTag> alcoholTags, List<RecipeIngredient> ingredients, List<RecipeStep> steps,
                List<PostComment> postComments, List<PostLike> postLikes, List<PostReport> postReports,
                List<UserRecipe> userRecipes ){
        this.id = id;
        this.user = user;
        this.haveRecipeYn = haveRecipeYn;
        this.content = content;
        this.images = images;
        this.alcoholTags = alcoholTags;
        this.ingredients = ingredients;
        this.steps = steps;
        this.postComments = postComments;
        this.postLikes = postLikes;
        this.postReports = postReports;
        this.userRecipes = userRecipes;
    }

    public void changeUser(User user){
        this.user = user;
    }

    public void updatePost(String content, String haveRecipeYn){
        this.content = content;
        this.haveRecipeYn = haveRecipeYn;
    }

}
