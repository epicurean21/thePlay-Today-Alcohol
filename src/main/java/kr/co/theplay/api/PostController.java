package kr.co.theplay.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.theplay.dto.Post.PostReqDto;
import kr.co.theplay.dto.zzz.ImageUploadToS3Dto;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            @RequestPart("request")PostReqDto postReqDto,
            @RequestPart("files") List<MultipartFile> files)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        postService.create(email, postReqDto, files);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }
}