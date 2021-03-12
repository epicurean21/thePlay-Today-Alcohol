package kr.co.theplay.service.api.security;

import kr.co.theplay.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        return userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }*/

}
