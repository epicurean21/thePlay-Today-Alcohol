package kr.co.theplay.api.images;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import kr.co.theplay.dto.images.ImageUploadDto;
import kr.co.theplay.service.api.advice.exception.ApiParamNotValidException;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.images.ImageService;
import kr.co.theplay.service.images.S3Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@Api(tags = {"202. Images (게시판 이미지)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "UserLogger")
@RestController
@AllArgsConstructor
public class ImageController {
    private S3Service s3Service;
    private ImageService imageService;
    private ResponseService responseService;

    @ApiOperation(value = "이미지 업로드", notes = "사진을 업로드한다.")
    @PostMapping(value = "/upload")
    public ResponseEntity<CommonResult> uploadImages(
            @ApiParam(value = "이미지 업로드 Dto", required = true)
                    ImageUploadDto imageUploadDto, MultipartFile[] file,
            @ApiIgnore Errors errors
    ) throws Exception {

        log.info("try upload info: : " + imageUploadDto.getFilePath());

        if (errors.hasErrors()) {
            throw new ApiParamNotValidException(errors);
        }

        /*
        게시글 업로드시에 File을 MultipartFile[]로 받고, S3Service내에 upload를 이용하여
        aws S3에 업로드하고,
        imageUploadDto내에 setFilePath를 해준 뒤, imageService에 saveImage로 local DB에 이미지 정보를 저장
        게시글 업로드시에는 밑에 로직을 추가하여 사진을 업로드하면 된다.
         */

        for (int i = 0; i < file.length; i++) {
            String imagePath = s3Service.upload(imageUploadDto.getFilePath(), file[i]);
            if (imagePath == "EXCEED") { // 업로드 하는 이미지 파일 크기
                throw new CommonBadRequestException("imageSizeExcessLimit");
            }
            imageUploadDto.setFilePath(imagePath);
            imageService.saveImage(imageUploadDto);
        }
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

}
