package kr.co.theplay.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.theplay.dto.post.*;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.api.common.model.ListResult;
import kr.co.theplay.service.api.common.model.SingleResult;
import kr.co.theplay.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = {"103. Post (게시글)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "PostLogger")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final ResponseService responseService;
    private final PostService postService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 작성하기", notes = "로그인한 회원이 게시글을 작성한다.")
    @PostMapping(value = "/post")
    public ResponseEntity<CommonResult> createPost(
            @RequestPart("request") PostReqDto postReqDto,
            @RequestPart("files") List<MultipartFile> files) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.create(email, postReqDto, files);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 신고하기", notes = "게시글을 신고한다")
    @PostMapping(value = "/post/report")
    public ResponseEntity<CommonResult> reportPost(@RequestBody PostReportReqDto postReportReqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.reportPost(email, postReportReqDto);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "메인 피드 게시글 조회", notes = "메인 피드에서 게시글을 최신순으로 조회한다.")
    @GetMapping(value = "/main-posts")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getPostsForMain(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PostResDto> postResDtos = postService.getPostsForMain(email, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "유저 메인 화면", notes = "유저의 메인 화면에서 게시글을 불러온다")
    @GetMapping(value = "/user/posts")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getUserPosts(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PostResDto> postResDtos = postService.getUserPosts(email, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 댓글 조회", notes = "게시글의 댓글과 대댓글을 조회한다")
    @GetMapping(value = "/posts/{postId}/comments")
    public ResponseEntity<ListResult<PostCommentDto>> getUserPostsLike(@PathVariable Long postId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        List<PostCommentDto> postCommentDto = postService.getComments(email, postId);
        ListResult<PostCommentDto> result = responseService.getListResult(postCommentDto);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 댓글 작성", notes = "댓글을 작성한다")
    @PostMapping(value = "/posts/{postId}/comments")
    public ResponseEntity<CommonResult> getUserPostsLike(@PathVariable Long postId, @RequestBody PostCommentReqDto postCommentReqDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.createComment(email, postId, postCommentReqDto);

        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 수정", notes = "로그인한 사용자가 본인의 게시글을 수정한다.")
    @PutMapping(value = "/post/{postId}")
    public ResponseEntity<CommonResult> updatePost(@PathVariable Long postId, @RequestBody PostReqDto postReqDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.updatePost(email, postId, postReqDto);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로잉 피드 게시글 조회", notes = "로그인한 사용자가 팔로잉하는 유저의 게시글 목록을 최신순으로 조회한다.")
    @GetMapping(value = "/following-posts")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getFollowingPosts(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PostResDto> postResDtos = postService.getFollowingPosts(email, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "레시피 저장/삭제", notes = "레시피를 저장 혹은 저장된 레시피를 삭제한다.")
    @PostMapping(value = "/recipe/{alcoholTagId}")
    public ResponseEntity<CommonResult> changeSaveRecipe(@PathVariable Long alcoholTagId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.changeSaveRecipe(email, alcoholTagId);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "유저 좋아요 누른 게시글", notes = "유저의 메인 화면에서 좋아요 누른 게시물들을 불러온다")
    @GetMapping(value = "/user/posts/like")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getUserLikedPosts(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PostResDto> postResDtos = postService.getUserLikedPosts(email, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "선택 유저 메인 화면", notes = "선택한 유저의 메인 화면에서 게시글들을 불러온다")
    @GetMapping(value = "/user/{userId}/posts")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getOtherUserPosts(@PathVariable Long userId,
                                                                            @RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PostResDto> postResDtos = postService.getOtherUserPosts(email, userId, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 좋아요 / 좋아요 취소", notes = "게시글을 좋아요 혹은 좋아요를 취소한다.")
    @PostMapping(value = "/post/{postId}/like")
    public ResponseEntity<SingleResult<PostLikeChangeResDto>> changeLikePost(@PathVariable Long postId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        PostLikeChangeResDto postLikeChangeResDto = postService.changeLikePost(email, postId);
        SingleResult<PostLikeChangeResDto> result = responseService.getSingleResult(postLikeChangeResDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 검색", notes = "검색한 게시글을 페이징으로 가져온다")
    @GetMapping(value = "/posts/search")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getSearchPosts(@RequestParam("recipeName") String recipeName,
                                                                         @RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }
        Page<PostResDto> postResDtos = postService.getSearchPosts(email, recipeName, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 삭제", notes = "로그인한 유저가 본인의 게시글을 삭제한다.")
    @DeleteMapping(value = "/post/{postId}")
    public ResponseEntity<CommonResult> deletePostById (@PathVariable Long postId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.deletePostById(email, postId);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

}
