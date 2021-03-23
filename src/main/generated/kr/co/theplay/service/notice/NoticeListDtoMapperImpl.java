package kr.co.theplay.service.notice;

import javax.annotation.Generated;
import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.dto.notice.NoticeListDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-18T17:48:44+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 15.0.1 (AdoptOpenJDK)"
)
public class NoticeListDtoMapperImpl implements NoticeListDtoMapper {

    @Override
    public Notice toEntity(NoticeListDto arg0) {
        if ( arg0 == null ) {
            return null;
        }

        Long id = null;
        String title = null;

        id = arg0.getId();
        title = arg0.getTitle();

        String content = null;

        Notice notice = new Notice( id, title, content );

        return notice;
    }

    @Override
    public NoticeListDto toDto(Notice arg0) {
        if ( arg0 == null ) {
            return null;
        }

        NoticeListDto noticeListDto = new NoticeListDto();

        return noticeListDto;
    }
}
