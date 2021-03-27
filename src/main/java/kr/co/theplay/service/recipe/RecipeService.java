package kr.co.theplay.service.recipe;

import kr.co.theplay.domain.post.*;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRecipe;
import kr.co.theplay.domain.user.UserRecipeRepository;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.post.AlcoholTagDto;
import kr.co.theplay.dto.post.RecipeIngredientDto;
import kr.co.theplay.dto.recipe.PopularRecipeDto;
import kr.co.theplay.dto.recipe.UserRecipeResDto;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import kr.co.theplay.service.zzz.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AlcoholTagRepository alcoholTagRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;

    public Page<UserRecipeResDto> getUserRecipes(String email, int number, int size) {
        // 사용자가 저장한 레시피들을 불러온다.
        Pageable pageable = PageRequest.of(number, size);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        Page<UserRecipe> userRecipes = userRecipeRepository.findByUserId(pageable, user.getId());

        List<UserRecipe> userRecipeList = userRecipes.getContent();

        List<UserRecipeResDto> dtos = userRecipes.stream().map(UserRecipeResDto::new).collect(Collectors.toList());

        for (int i = 0; i < dtos.size(); i++) {
            dtos.get(i).setPostId(userRecipeList.get(i).getPost().getId());

            // alcoholTag 매칭 (레시피인것)
            AlcoholTag alcoholTag = alcoholTagRepository.findByPostId(dtos.get(i).getPostId());
            AlcoholTagDto alcoholTagDto = new AlcoholTagDto(alcoholTag);
            dtos.get(i).setAlcoholTag(alcoholTagDto);

            List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
            List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
            dtos.get(i).setIngredients(ingredientDtos);
        }

        return new PageImpl<>(dtos, pageable, userRecipes.getTotalElements());
    }
}
