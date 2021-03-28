package kr.co.theplay.service.recipe;

import kr.co.theplay.domain.post.*;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRecipe;
import kr.co.theplay.domain.user.UserRecipeRepository;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.post.*;
import kr.co.theplay.dto.recipe.PopularRecipeDto;
import kr.co.theplay.dto.recipe.UserRecipeResDto;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import kr.co.theplay.service.zzz.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService {

    private final AlcoholTagRepository alcoholTagRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserRepository userRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;

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
        Page<UserRecipe> userRecipes = userRecipeRepository.findByUserIdOrderByCreatedDateDesc(pageable, user.getId());

        List<UserRecipe> userRecipeList = userRecipes.getContent();

        List<UserRecipeResDto> dtos = userRecipes.stream().map(UserRecipeResDto::new).collect(Collectors.toList());

        for (int i = 0; i < dtos.size(); i++) {
            dtos.get(i).setPostId(userRecipeList.get(i).getAlcoholTag().getPost().getId());

            // alcoholTag 매칭 (레시피인것)
            AlcoholTag alcoholTag = alcoholTagRepository.findRecipesByPostId(dtos.get(i).getPostId());
            AlcoholTagDto alcoholTagDto = new AlcoholTagDto(alcoholTag);
            dtos.get(i).setAlcoholTag(alcoholTagDto);

            List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
            List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
            dtos.get(i).setIngredients(ingredientDtos);
        }

        return new PageImpl<>(dtos, pageable, userRecipes.getTotalElements());
    }

    public List<UserRecipeResDto> getUserSearchRecipe(String email, String recipeName) {
        List<UserRecipe> userRecipes = userRecipeRepository.findByKeyword(email, recipeName);
        List<UserRecipeResDto> dtos = userRecipes.stream().map(UserRecipeResDto::new).collect(Collectors.toList());

        for (int i = 0; i < userRecipes.size(); i++) {
            dtos.get(i).setPostId(userRecipes.get(i).getAlcoholTag().getPost().getId());

            // alcoholTag 매칭 (레시피인것)
            AlcoholTag alcoholTag = alcoholTagRepository.findRecipesByPostId(dtos.get(i).getPostId());
            AlcoholTagDto alcoholTagDto = new AlcoholTagDto(alcoholTag);
            dtos.get(i).setAlcoholTag(alcoholTagDto);

            List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
            List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
            dtos.get(i).setIngredients(ingredientDtos);
        }
        return dtos;
    }

    public Page<PostResDto> getPopularRecipesByTagName(String email, String tagName, int number, int size) {

        Pageable pageable = PageRequest.of(number, size);

        //태그에 해당되는 post에 대해 좋아요 개수와 postId를 인기순 조회 (좋아요 0개도 조회)
        Page<Object []> postInfos = alcoholTagRepository.findByNameOrderByLikeCnt(tagName, pageable);
        List<Object []> postInfoList = postInfos.getContent();

        //태그에 해당하는 각 post 정보를 postResDto로 매칭
        List<PostResDto> dtos = new ArrayList<>();

        for(int i = 0; i < postInfoList.size(); i++){

            Post post = postRepository.getRecipeByPostId( ((BigInteger)postInfoList.get(i)[1]).longValue())
                    .orElseThrow(() -> new CommonNotFoundException("pageNotFound"));
            PostResDto dto = new PostResDto(post);

            // 좋아요 여부
            if(postLikeRepository.findByPostIdAndUserEmail(post.getId(), email).isPresent()){
                dto.setPostLikeYn("Y");
            }else{
                dto.setPostLikeYn("N");
            }

            // 레시피 저장 여부 (레시피가 있는 애들만 조회된 상태)
            if(userRecipeRepository.findByPostIdAndUserEmail(post.getId(), email).isPresent()){
                dto.setSaveRecipeYn("Y");
            }else{
                dto.setSaveRecipeYn("N");
            }

            // 댓글 수
            Long commentCnt = postCommentRepository.getCountOfPostComment(post.getId());
            dto.setCommentCnt(commentCnt);

            // 대표댓글
            if(commentCnt != 0){
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(post.getId(), (long) 0);
                dto.setComment(comment.getContent());
            }else{
                dto.setComment("N");
            }

            //이미지들
            List<PostImageDto> imageDtos = post.getImages().stream().map(PostImageDto::new).collect(Collectors.toList());
            dto.setImages(imageDtos);

            // 술 태그들
            List<AlcoholTagDto> alcoholTagDtos = post.getAlcoholTags().stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dto.setAlcoholTags(alcoholTagDtos);

            // 재료들
            List<RecipeIngredientDto> ingredientDtos = post.getIngredients().stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
            dto.setIngredients(ingredientDtos);

            // step들
            List<RecipeStepDto> stepDtos = post.getSteps().stream().map(RecipeStepDto::new).collect(Collectors.toList());
            dto.setSteps(stepDtos);

            dtos.add(dto);
        }

        return new PageImpl<>(dtos, pageable, postInfos.getTotalElements());
    }
}
