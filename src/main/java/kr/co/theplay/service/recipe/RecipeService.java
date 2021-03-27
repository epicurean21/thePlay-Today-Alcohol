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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService {

    private final AlcoholTagRepository alcoholTagRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserRepository userRepository;
    private final UserRecipeRepository userRecipeRepository;

    public Page<PopularRecipeDto> getPopularRecipes(int number, int size) {

        Pageable pageable = PageRequest.of(number, size);

        // 술 태그명을 인기순으로 paging 조회 : 좋아요 수 cnt(Long), alcoholName(String)
        Page<Object[]> tagNames = alcoholTagRepository.findPopularTags(pageable);
        List<Object[]> tagNameList = tagNames.getContent();
        List<PopularRecipeDto> dtos = new ArrayList<>();

        // 각 tagName 별로 10개의 상위 인기 post 조회
        // response Dto에 필요한 정보 : alcoholTagName, 최고 인기 게시글의 ingredients(dto), 10개의 images(List<String>)
        for (int i = 0; i < tagNameList.size(); i++) {

            //tagName
            String tagName = (String) tagNameList.get(i)[1];

            // postId(Long), 좋아요 수 cnt(Long), 첫이미지(String, filePath) 순 조회함. postId는 resDto에 없지만 최고 인기 게시글의 재료를 알기 위해 조회함.
            List<Object[]> tenPopularPostInfos = alcoholTagRepository.find10PopularImagesByAlcoholTagName(tagName);

            //이미지 path 목록을 가진 list 생성
            List<String> images = tenPopularPostInfos.stream().map(m ->
                    "https://" + S3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + (String) m[2])
                    .collect(Collectors.toList());

            //최고 인기 레시피의 postId로 재료 정보 dto 생성
            List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(((BigInteger) tenPopularPostInfos.get(0)[0]).longValue());
            List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());

            PopularRecipeDto createdDto = PopularRecipeDto.builder().alcoholTagName(tagName).ingredients(ingredientDtos).images(images).build();
            dtos.add(createdDto);
        }

        return new PageImpl<>(dtos, pageable, tagNames.getTotalElements());
    }

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
