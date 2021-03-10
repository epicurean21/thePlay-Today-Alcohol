package kr.co.theplay.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.dto.user.SignInDto;
import kr.co.theplay.dto.user.SignUpDto;
import kr.co.theplay.service.api.advice.exception.ApiParamNotValidException;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.api.common.model.SingleResult;
import kr.co.theplay.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = {"101. User (회원인증 및 회원정보)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "UserLogger")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;

    private final PasswordEncoder passwordEncoder;

    //회원가입
    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/sign-up")
    public ResponseEntity<CommonResult> signUp(
            @ApiParam(value = "회원가입용 Dto", required = true) @RequestBody @Valid SignUpDto signUpDto,
            @ApiIgnore Errors errors
            )
    {
        log.info("try login info : "+ signUpDto.getEmail());

        if(errors.hasErrors()){
            throw new ApiParamNotValidException(errors);
        }

        //valid를 통과했다면 password, confirmPassword 비교
        if(! signUpDto.getPassword().equals(signUpDto.getConfirmPassword())){
            throw new CommonBadRequestException("passwordNotMatched");
        }

        String password = signUpDto.getPassword();
        signUpDto.encodePassword(passwordEncoder.encode(password));

        userService.signUp(signUpDto);

        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);

    }

    @PostMapping("/sign-in")
    public ResponseEntity<SingleResult<String>> signIn(@RequestBody SignInDto signInDto){

        String token = userService.signIn(signInDto);
        SingleResult<String> result = responseService.getSingleResult(token);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
