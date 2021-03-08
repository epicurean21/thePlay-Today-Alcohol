package kr.co.theplay.service.user;

import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.domain.user.UserRole;
import kr.co.theplay.domain.user.UserRoleRepository;
import kr.co.theplay.dto.user.SignUpDto;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public void signUp(SignUpDto signUpDto) {

        User user = SignUpDtoMapper.INSTANCE.toEntity(signUpDto);

        //이미 가입한 회원인지 email 확인
        if(userRepository.findByEmail(signUpDto.getEmail()).isPresent()){
            throw new CommonConflictException("userDuplication");
        }

        //닉네임 중복 확인
        if(userRepository.findByNickname(signUpDto.getNickname()).isPresent()){
            throw new CommonConflictException("nicknameDuplication");
        }

        userRepository.save(user);

        //Role 생성
        UserRole userRole = UserRole.builder().user(user).roleName("ROLE_USER").build();
        userRoleRepository.save(userRole);
    }
}
