package kr.co.theplay.api.zzz;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.parser.JSONParser;
import kr.co.theplay.dto.zzz.ImageUploadToS3Dto;
import kr.co.theplay.dto.zzz.PostDto;
import kr.co.theplay.dto.zzz.ZUserReqDto;
import kr.co.theplay.service.api.advice.exception.ApiParamNotValidException;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.api.common.model.ListResult;
import kr.co.theplay.service.api.common.model.SingleResult;
import kr.co.theplay.service.zzz.ImageService;
import kr.co.theplay.service.zzz.S3Service;
import kr.co.theplay.service.zzz.ZUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags = {"999. 테스트용 API"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "ZZZ Logger")
@RequiredArgsConstructor
@RestController
public class ZUserController {

    private final ResponseService responseService;
    private final ZUserService zUserService;
    private final S3Service s3Service;
    private final ImageService imageService;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @ApiOperation(value = "회원 추가", notes = "새로운 회원을 추가한다.")
    @PostMapping(value = "/user")
    public ResponseEntity<CommonResult> saveZUser(@RequestBody ZUserReqDto zUserReqDto) {
        zUserService.saveZUser(zUserReqDto);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @PutMapping(value = "/user/{userId}")
    public ResponseEntity<CommonResult> updateZUser(@PathVariable Long userId, @RequestBody ZUserReqDto zUserReqDto) {
        zUserService.updateZUser(userId, zUserReqDto);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiOperation(value = "이미지 업로드", notes = "사진을 업로드한다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResult> uploadImages(
            @RequestPart("dto") ImageUploadToS3Dto imageUploadToS3Dto,
            @RequestPart("files") List<MultipartFile> files) throws Exception {
        log.info("try upload info: : FILE UPLOAD");
        imageService.savePostDto(files, imageUploadToS3Dto);

        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @GetMapping(value = "/images")
    public ResponseEntity<SingleResult<PostDto>> getImage(){
        PostDto postDto =  imageService.getImage();
        SingleResult<PostDto> result = responseService.getSingleResult(postDto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/images")
    public ResponseEntity<CommonResult> deleteImages(){
        imageService.deleteImages();
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }
}
