package kr.co.theplay.api;

import io.swagger.annotations.*;
import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.dto.notice.AlarmResDto;
import kr.co.theplay.dto.notice.NoticeListDto;
import kr.co.theplay.dto.notice.NoticeSingleDto;
import kr.co.theplay.dto.user.*;
import kr.co.theplay.service.api.advice.exception.ApiParamNotValidException;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.api.common.model.ListResult;
import kr.co.theplay.service.api.common.model.SingleResult;
import kr.co.theplay.service.notice.AlarmService;
import kr.co.theplay.service.notice.NoticeService;
import kr.co.theplay.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"101. User (회원인증 및 회원정보)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "UserLogger")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;
    private final NoticeService noticeService;
    private final AlarmService alarmService;

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
            @ApiParam(value = "닉네임 변경 Dto", required = true) @RequestBody UserChangeNicknameDto userChangeNicknameDto,
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

        userService.updateUserNickname(userChangeNicknameDto, email);
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
    public ResponseEntity<SingleResult<UserChangePrivacyResDto>> changePrivacyYn() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        UserChangePrivacyResDto userChangePrivacyResDto = userService.changePrivacyYn(email);
        SingleResult<UserChangePrivacyResDto> result = responseService.getSingleResult(userChangePrivacyResDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
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

        userService.changePassword(userChangePasswordDto, email);
        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "개인 정보 수정", notes = "개인 정보 수정 화면")
    @GetMapping(value = "/user/setting")
    public ResponseEntity<SingleResult<UserSettingsDto>> userSettings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }
        UserSettingsDto userSettingsDto = userService.getUserSettings(email);
        SingleResult<UserSettingsDto> result = responseService.getSingleResult(userSettingsDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "공지사항 목록 조회", notes = "공지사항 목록 조회")
    @GetMapping(value = "/user/notice")
    public ResponseEntity<ListResult<NoticeListDto>> getNoticeList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        List<NoticeListDto> noticeListDto = noticeService.getNoticeList();
        ListResult<NoticeListDto> result = responseService.getListResult(noticeListDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "공지사항 단건 조회", notes = "공지사항 단건 조회")
    @GetMapping(value = "/user/notice/{noticeId}")
    public ResponseEntity<SingleResult<NoticeSingleDto>> getNoticeSingle(@PathVariable Long noticeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        NoticeSingleDto noticeSingleDto = noticeService.getNoticeSingle(noticeId);
        SingleResult<NoticeSingleDto> result = responseService.getSingleResult(noticeSingleDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "회원가임 랜덤 닉네임 조회", notes = "회원가입 랜덤 닉네임 조회")
    @GetMapping(value = "/random-nickname")
    public ResponseEntity<ListResult<RandomNicknameDto>> getRandomNickname() {
        List<RandomNicknameDto> randomNicknameDto = userService.getRandomNickname();
        ListResult<RandomNicknameDto> result = responseService.getListResult(randomNicknameDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "유저 메인 상단 정보", notes = "유저 메인 상단 정보 [게시물, 좋아요, 팔로워, 나의 레시피]")
    @GetMapping(value = "/user/main-info")
    public ResponseEntity<SingleResult<UserMainInfoDto>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        UserMainInfoDto userMainInfoDto = userService.getUserMainInfo(email);
        SingleResult<UserMainInfoDto> result = responseService.getSingleResult(userMainInfoDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "선택 유저 메인 상단 정보", notes = "선택 유저 메인 상단 정보 [게시물, 좋아요, 팔로워, 나의 레시피]")
    @GetMapping(value = "/user/{userId}/main-info")
    public ResponseEntity<SingleResult<UserMainInfoDto>> getOtherUserInfo(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        UserMainInfoDto userMainInfoDto = userService.getOtherUserIngo(email, userId);
        SingleResult<UserMainInfoDto> result = responseService.getSingleResult(userMainInfoDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "알람 목록 조회", notes = "알람 목록을 조회한다")
    @GetMapping(value = "/alarms")
    public ResponseEntity<ListResult<AlarmResDto>> getAlarm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        List<AlarmResDto> alarmResDto = alarmService.getAlarms(email);
        ListResult<AlarmResDto> result = responseService.getListResult(alarmResDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "알람 단건 조회", notes = "단건 알람을 조회하여 읽는다.")
    @GetMapping(value = "/alarm/{alarmId}")
    public ResponseEntity<SingleResult<AlarmResDto>> getSingleAlarm(@PathVariable Long alarmId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        AlarmResDto alarmResDto = alarmService.getSingleAlarm(email, alarmId);
        SingleResult<AlarmResDto> result = responseService.getSingleResult(alarmResDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "개정 공개 비공개 여부", notes = "로그인 된 개정의 공개/비공개 여부를 조회한다")
    @GetMapping(value = "/user/show-yn")
    public ResponseEntity<SingleResult<UserChangePrivacyResDto>> getUserPrivacy() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        UserChangePrivacyResDto userChangePrivacyResDto = userService.getUserPrivacy(email);
        SingleResult<UserChangePrivacyResDto> result = responseService.getSingleResult(userChangePrivacyResDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
