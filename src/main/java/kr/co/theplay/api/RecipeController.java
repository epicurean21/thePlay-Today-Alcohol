package kr.co.theplay.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.theplay.dto.recipe.PopularRecipeDto;
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
    private final RecipeService recipeService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "메인 피드 게시글 조회", notes = "메인 피드에서 게시글을 최신순으로 조회한다.")
    @GetMapping(value = "/popular-recipes")
    public ResponseEntity<SingleResult<Page<PopularRecipeDto>>> getPopularRecipes(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PopularRecipeDto> popularRecipeDtos = recipeService.getPopularRecipes(number, size);
        SingleResult<Page<PopularRecipeDto>> result = responseService.getSingleResult(popularRecipeDtos);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
