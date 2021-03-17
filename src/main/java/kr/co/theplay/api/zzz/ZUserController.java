package kr.co.theplay.api.zzz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import kr.co.theplay.dto.zzz.ImageUploadDto;
import kr.co.theplay.dto.zzz.ImageUploadToS3Dto;
import kr.co.theplay.dto.zzz.ZUserReqDto;
import kr.co.theplay.service.api.advice.exception.ApiParamNotValidException;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.zzz.ImageService;
import kr.co.theplay.service.zzz.S3Service;
import kr.co.theplay.service.zzz.ZUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = {"999. ZZZ (예시 API)"})
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
    @PostMapping(value = "/upload")
    public ResponseEntity<CommonResult> uploadImages(
            @ApiParam(value = "이미지 업로드 파일", required = true)
                    ImageUploadToS3Dto imageUploadToS3Dto,
            @ApiIgnore Errors errors
    ) throws Exception {

        log.info("try upload info: : FILE UPLOAD");

        if (errors.hasErrors()) {
            throw new ApiParamNotValidException(errors);
        }

        /*
        게시글 업로드시에 File을 MultipartFile[]로 받고, S3Service내에 upload를 이용하여
        aws S3에 업로드하고,
        imageUploadDto내에 setFilePath를 해준 뒤, imageService에 saveImage로 local DB에 이미지 정보를 저장
        게시글 업로드시에는 밑에 로직을 추가하여 사진을 업로드하면 된다.
         */

        ImageUploadDto imageUploadDto = new ImageUploadDto();
        for (int i = 0; i < imageUploadToS3Dto.getFile().length; i++) {
            String imagePath = s3Service.upload(imageUploadToS3Dto.getFile()[i]);
            if (imagePath == "EXCEED") { // 업로드 하는 이미지 파일 크기
                throw new CommonBadRequestException("imageSizeExcessLimit");
            }
            imageUploadDto.setFilePath(imagePath);
            imageService.saveImage(imageUploadDto);
        }
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }
}
