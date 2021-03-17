package kr.co.theplay.api;

import io.swagger.annotations.*;
import kr.co.theplay.api.config.security.JwtTokenProvider;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.user.*;
import kr.co.theplay.service.api.advice.exception.ApiParamNotValidException;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.api.common.model.SingleResult;
import kr.co.theplay.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    //회원가입
    @ApiOperation(value = "회원가입", notes = "회원가입을 하고 토큰을 발급받는다.")
    @PostMapping("/sign-up")
    public ResponseEntity<SingleResult<String>> signUp(
            @ApiParam(value = "회원가입용 Dto", required = true) @RequestBody @Valid SignUpDto signUpDto,
            @ApiIgnore Errors errors
    ) {
//        log.info("try login info : " + signUpDto.getEmail());

        if (errors.hasErrors()) {
            throw new ApiParamNotValidException(errors);
        }

        //valid를 통과했다면 password, confirmPassword 비교
        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new CommonBadRequestException("passwordNotMatched");
        }

        String password = signUpDto.getPassword();
        signUpDto.encodePassword(passwordEncoder.encode(password));

        String token = userService.signUp(signUpDto);
        SingleResult<String> result = responseService.getSingleResult(token);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @ApiOperation(value = "로그인", notes = "로그인을 하고 Token을 받는다.")
    @PostMapping(value = "/sign-in")
    public ResponseEntity<SingleResult<String>> signIn(
            @ApiParam(value = "로그인 Dto", required = true) @RequestBody SignInDto signInDto,
            @ApiIgnore Errors errors
    ) {
//        log.info("try sign in info : " + signInDto.getEmail());

        if (errors.hasErrors()) {
            throw new ApiParamNotValidException(errors);
        }

        // 여기서 validation을 해준다
        userService.signIn(signInDto);

        // 존재하는 회원, 올바른 비밀번호 입력이니 token을 발급받는다.
        String token = userService.getLoginToken(signInDto);
        SingleResult<String> result = responseService.getSingleResult(token);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 닉네임 변경", notes = "회원 닉네임을 변경한다.")
    @PutMapping(value = "/user/nickname")
    public ResponseEntity<CommonResult> changeNickname(
            @ApiParam(value = "닉네임 변경 Dto", required = true) @RequestBody UserUpdateNicknameDto userUpdateNicknameDto,
            @ApiIgnore Errors errors
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }
        if (errors.hasErrors()) {
            throw new ApiParamNotValidException(errors);
        }

        userService.updateUserNickname(userUpdateNicknameDto, email);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiOperation(value = "비밀번호 찾기", notes = "비밀번호 찾기 이메일 전송")
    @PostMapping(value = "/user/password")
    public ResponseEntity<CommonResult> findPassword(
            @ApiParam(value = "비밀번호 찾기 이메일 Dto", required = true) @RequestBody UserFindPasswordDto userFindPasswordDto,
            @ApiIgnore Errors errors
    ) {
        if (errors.hasErrors())
            throw new ApiParamNotValidException(errors);

        UserSendEmailDto userSendEmailDto = userService.createMailAndChangePassword(userFindPasswordDto.getEmail());
        userService.sendEmail(userSendEmailDto);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 계정 비공개 여부 변경", notes = "회원 계정의 비공개 여부를 변경한다.")
    @PutMapping(value = "/user/show-yn")
    public ResponseEntity<CommonResult> changePrivacyYn() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        userService.changePrivacyYn(email);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 비밀번호 변경", notes = "회원 계정의 비밀번호를 변경한다")
    @PutMapping(value = "/user/password")
    public ResponseEntity<CommonResult> changePassword(
            @ApiParam(value = "비밀번호 변경 Dto", required = true) @RequestBody @Valid UserChangePasswordDto userChangePasswordDto,
            @ApiIgnore Errors errors
    ) {
        if (errors.hasErrors()) {
            throw new ApiParamNotValidException(errors);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        //valid를 통과했다면 password, confirmPassword 비교
        if (!userChangePasswordDto.getPassword().equals(userChangePasswordDto.getConfirmPassword())) {
            throw new CommonBadRequestException("passwordNotMatched");
        }

        userService.changePassword(userChangePasswordDto, email);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

}
