package kr.co.theplay.api;

import io.swagger.annotations.*;
import kr.co.theplay.dto.recipe.UserRecipeResDto;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.SingleResult;
import kr.co.theplay.service.post.PostService;
import kr.co.theplay.service.recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"104. Recipe (레시피)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "RecipeLogger")
@RequiredArgsConstructor
@RestController
public class RecipeController {
    private final ResponseService responseService;
    private final PostService postService;
    private final RecipeService recipeService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "유저 나의 레시피 목록 불러오기", notes = "유저 나의 레시피 (저장한 레시피) 목록을 페이징으로 가져온다")
    @GetMapping(value = "/user/recipe")
    public ResponseEntity<SingleResult<Page<UserRecipeResDto>>> getUserRecipes(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<UserRecipeResDto> userRecipeResDtos = recipeService.getUserRecipes(email, number, size);
        SingleResult<Page<UserRecipeResDto>> result = responseService.getSingleResult(userRecipeResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
