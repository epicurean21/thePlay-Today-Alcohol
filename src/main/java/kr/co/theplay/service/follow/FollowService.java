package kr.co.theplay.service.follow;

import kr.co.theplay.domain.follow.Block;
import kr.co.theplay.domain.follow.BlockRepository;
import kr.co.theplay.domain.follow.Follow;
import kr.co.theplay.domain.follow.FollowRepository;
import kr.co.theplay.domain.notice.Alarm;
import kr.co.theplay.domain.notice.AlarmRepository;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.follow.FollowUserDto;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import kr.co.theplay.service.api.common.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final BlockRepository blockRepository;
    private final AlarmRepository alarmRepository;
    private final ResponseService responseService;

    @Transactional
    public void followUser(String email, Long userId) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        if (userId == user.getId()) {
            //본인을 팔로우하려는 경우
            throw new CommonNotFoundException("followedUserNotFound");
        }
        User followedUser = userRepository.findById(userId).orElseThrow(() -> new CommonNotFoundException("followedUserNotFound"));

        //이미 팔로우 관계가 존재하는 경우
        if (followRepository.findByUserAndUserFollow(user, followedUser).isPresent()) {
            throw new CommonConflictException("followConflict");
        }

        Follow follow = Follow.builder().user(user).userFollow(followedUser).build();
        followRepository.save(follow);

        uploadFollowingAlarm(user, followedUser);
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

    public List<FollowUserDto> getOtherUserFollower(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        if (user.getPrivacyYn().equals("Y")) {
            throw new CommonBadRequestException("userPrivacyInvaded");
        }
        List<Follow> follows = followRepository.findFollowersByUser(user.getEmail());
        List<FollowUserDto> followUserDtos = new ArrayList<>();
        follows.forEach(f -> followUserDtos.add(
                FollowUserDto.builder()
                        .id(f.getUser().getId())
                        .nickname(f.getUser().getNickname())
                        .build()
        ));
        return followUserDtos;
    }

    @Transactional
    public void deleteFollower(String email, Long id) {
        // email은 로그인 한 사용자꺼, id 삭제하고자 하는 유저

        // 취소하고자 하는 유저가 존재하지 않을경우
        User user = userRepository.findById(id).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        Follow follow = followRepository.findFollowerById(email, id);

        // 나를 팔로잉 하고 있지 않은 회원일 경우
        if (follow == null) {
            throw new CommonNotFoundException("followerUserNotFound");
        }

        followRepository.delete(follow);
    }

    @Transactional
    public void deleteFollowing(String email, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        Follow follow = followRepository.findFollowingById(email, id);

        if (follow == null) {
            throw new CommonNotFoundException("followingUserNotFound");
        }

        followRepository.delete(follow);
    }

    @Transactional
    public void blockFollower(String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        User userBlock = userRepository.findById(id).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        if (blockRepository.findByUserAndUserBlock(user, userBlock).isPresent()) {
            throw new CommonConflictException("blockConflict");
        }

        Block block = Block.builder().user(user).userBlock(userBlock).build();
        blockRepository.save(block);
        deleteFollower(user.getEmail(), userBlock.getId());
    }

    @Transactional
    public void uploadFollowingAlarm(User user, User followedUser) {
        Alarm alarm = Alarm.builder()
                .content(
                        followedUser.getNickname() + "님이 회원님을 팔로잉 합니다."
                ).user(followedUser).userSend(user).readYn("N").type("follow").build();
        alarmRepository.save(alarm);
    }
}
