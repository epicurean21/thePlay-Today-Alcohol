package kr.co.theplay.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.theplay.domain.post.PostRepository;
import kr.co.theplay.dto.Post.PostReportReqDto;
import kr.co.theplay.dto.Post.PostReqDto;
import kr.co.theplay.dto.Post.PostResDto;
import kr.co.theplay.dto.zzz.ImageUploadToS3Dto;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
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
    @ApiOperation(value = "메인 피드 게시글 조회", notes = "메인 피드에서 게시글을 조회한다,")
    @GetMapping(value = "/main-posts")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getPostsForMain(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size){

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
    public ResponseEntity<SingleResult<Page<PostResDto>>> getUserPosts(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PostResDto> postResDtos = postService.getUserPosts(email, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

/*    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 수정", notes = "로그인한 사용자가 본인의 게시글을 수정한다.")
    @PutMapping(value = "/v1/post/{postId}")
    public ResponseEntity<CommonResult> updatePost(@PathVariable("postId") Long postId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.updatePost()
    }*/
}
