package kr.co.theplay.service.user;

import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.user.UserSendEmailDto;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserFindPasswordService {
    @Autowired
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "todaysalcohol@gmail.com";

    @Transactional
    public UserSendEmailDto createMailAndChangePassword(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new CommonConflictException("userNotFound");
        }

        String tempPassword = getTempPassword();
        UserSendEmailDto userSendEmailDto = new UserSendEmailDto();
        userSendEmailDto.setEmail(email);
        userSendEmailDto.setTitle("[오늘 한 주] " + email + " 님의 '오늘 한 주' 임시 비밀번호 안내 이메일입니다.");
        userSendEmailDto.setMessage("안녕하세요, 오늘 한 주 임시 비밀번호 안내 관련 이메일 입니다.\n" + "[" + email
                + "] 님의 임시 비밀번호는 \n Password: [" + tempPassword + "] 입니다.\n" + "해당 비밀번호로 로그인 하시고 비밀번호를 변경하여 주시기 바랍니다.");
        updatePassword(tempPassword, email);
        return userSendEmailDto;
    }

    // 임시 비밀번호 생성기
    public String getTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    @Transactional
    public void updatePassword(String tempPassword, String email) {
        String password = passwordEncoder.encode(tempPassword);
        userService.updateUserPassword(email, password);
    }

    public void sendEmail(UserSendEmailDto userSendEmailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userSendEmailDto.getEmail());
        message.setFrom(UserFindPasswordService.FROM_ADDRESS);
        message.setSubject(userSendEmailDto.getTitle());
        message.setText(userSendEmailDto.getMessage());
        mailSender.send(message);
    }
}
