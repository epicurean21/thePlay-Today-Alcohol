package kr.co.theplay.service.user;

import kr.co.theplay.api.config.security.JwtTokenProvider;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.domain.user.UserRole;
import kr.co.theplay.domain.user.UserRoleRepository;
import kr.co.theplay.dto.user.SignInDto;
import kr.co.theplay.dto.user.SignUpDto;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignUpDto signUpDto) {

        User user = SignUpDtoMapper.INSTANCE.toEntity(signUpDto);

        //이미 가입한 회원인지 email 확인
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new CommonConflictException("userDuplication");
        }

        //닉네임 중복 확인
        if (userRepository.findByNickname(signUpDto.getNickname()).isPresent()) {
            throw new CommonConflictException("nicknameDuplication");
        }

        userRepository.save(user);

        //Role 생성
        UserRole userRole = UserRole.builder().user(user).roleName("ROLE_USER").build();
        userRoleRepository.save(userRole);
    }

    @Transactional
    public void updateUserPassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonConflictException("userNotFound"));
        user.updateUserPassword(password);
        userRepository.save(user);
    }


    public String signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        if(!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())){
            throw new CommonBadRequestException("passwordDenied");
        }
        List<String> roles = new ArrayList<>();
        roles.add(user.getUserRole().getRoleName());
        return jwtTokenProvider.createToken(String.valueOf(user.getId()), roles);
    }
}
