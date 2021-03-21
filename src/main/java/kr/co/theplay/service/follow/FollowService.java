package kr.co.theplay.service.follow;

import kr.co.theplay.domain.follow.Follow;
import kr.co.theplay.domain.follow.FollowRepository;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.follow.FollowUserDto;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void followUser(String email, Long userId) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        if (userId == user.getId()) {
            //본인을 팔로우하려는 경우
            throw new CommonNotFoundException("followedUserNotFound");
        }
        User followedUser = userRepository.findById(userId).orElseThrow(() -> new CommonNotFoundException("followedUserNotFound"));

        Follow follow = Follow.builder().user(user).userFollow(followedUser).build();
        followRepository.save(follow);
    }

    public List<FollowUserDto> getFollowings(String email) {

        List<Follow> follows = followRepository.findFollowingsByUser(email);

        List<FollowUserDto> followUserDtos = new ArrayList<>();
        follows.forEach(f -> followUserDtos.add(
                FollowUserDto.builder().id(f.getUserFollow().getId()).nickname(f.getUserFollow().getNickname()).build()
        ));

        return followUserDtos;
    }

    public List<FollowUserDto> getFollowers(String email) {
        List<Follow> follows = followRepository.findFollowersByUser(email);
        List<FollowUserDto> followUserDtos = new ArrayList<>();
        follows.forEach(f -> followUserDtos.add(
                FollowUserDto.builder()
                        .id(f.getUser().getId())
                        .nickname(f.getUser().getNickname())
                        .build()
        ));
        return followUserDtos;
    }
}
