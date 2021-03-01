package kr.co.theplay.api.zzz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.theplay.dto.zzz.ZUserReqDto;
import kr.co.theplay.service.common.ResponseService;
import kr.co.theplay.service.common.model.CommonResult;
import kr.co.theplay.service.zzz.ZUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"999. ZZZ (예시 API)"})
@RequestMapping(value = "/api")
@Slf4j(topic = "ZZZ Logger")
@RequiredArgsConstructor
@RestController
public class ZUserController {

    private final ResponseService responseService;
    private final ZUserService zUserService;

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @ApiOperation(value = "회원 추가", notes = "새로운 회원을 추가한다.")
    @PostMapping(value = "/user")
    public ResponseEntity<CommonResult> saveZUser(@RequestBody ZUserReqDto zUserReqDto) {
        zUserService.saveZUser(zUserReqDto);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }
}
