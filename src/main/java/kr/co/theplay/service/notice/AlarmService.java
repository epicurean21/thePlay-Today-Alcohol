package kr.co.theplay.service.notice;

import kr.co.theplay.domain.notice.Alarm;
import kr.co.theplay.domain.notice.AlarmRepository;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.notice.AlarmResDto;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlarmService {
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public List<AlarmResDto> getAlarms(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        List<Alarm> alarm = alarmRepository.findByUserOrderByCreatedDateDesc(user);
        List<AlarmResDto> alarmResDtos = new ArrayList<>();
        alarm.forEach(a -> alarmResDtos.add(
                AlarmResDto.builder().id(a.getId()).content(a.getContent()).readYn(a.getReadYn()).type(a.getType()).build()
        ));

        // 만약 읽지 않은 알림들이 있다면 다 읽은걸로
        for (int i = 0; i < alarm.size(); i++) {
            if (alarm.get(i).getReadYn().equals("N")) {
                alarm.get(i).changeReadYn("Y");
                alarmRepository.save(alarm.get(i));
            }
        }
        return alarmResDtos;
    }

    @Transactional
    public AlarmResDto getSingleAlarm(String email, Long alarmId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(() -> new CommonNotFoundException("alarmNotFound"));

        if (alarm.getUser().getId() != user.getId()) {
            throw new CommonBadRequestException("alarmNotAllowd"); // 허용되지 않는 알림에 접근할 때
        }

        if (alarm.getReadYn().equals("N")) { // 읽지 않은 알림이었다면
            alarm.changeReadYn("Y");
            alarmRepository.save(alarm);
        }
        AlarmResDto alarmResDto = AlarmResDto.builder().id(alarm.getId()).content(alarm.getContent()).readYn(alarm.getReadYn()).type(alarm.getType()).build();
        return alarmResDto;
    }
}
