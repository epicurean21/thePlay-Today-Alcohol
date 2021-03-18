package kr.co.theplay.service.notice;

import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.domain.notice.NoticeRepository;
import kr.co.theplay.dto.notice.NoticeListDto;
import kr.co.theplay.dto.notice.NoticeSingleDto;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<NoticeListDto> getNoticeList() {
        /*
        NoticeListDto에는 notice의 id와 title이 있다.
        Notice 객체를 다 가져온 다음 (order by createdDate desc)
        이걸 NoticeListDto에 객체 하나하나 mapping 해야한다.
         */
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedDateDesc();
        List<NoticeListDto> noticeListDto = new ArrayList<>();
/*
        notices.forEach(
               f -> noticeListDto.add(NoticeListDtoMapper.INSTANCE.toDto(f))
        );
*/
        notices.forEach(f -> noticeListDto.add(
                NoticeListDto.builder()
                        .id(f.getId())
                        .title(f.getTitle())
                        .build()
        ));
        return noticeListDto;
    }

    public NoticeSingleDto getNoticeSingle(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CommonBadRequestException("noticeDoesNotExists"));
        NoticeSingleDto noticeSingleDto = NoticeSingleDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .lastModifiedDate(notice.getLastModifiedDate())
                .build();
        return noticeSingleDto;
    }
}
