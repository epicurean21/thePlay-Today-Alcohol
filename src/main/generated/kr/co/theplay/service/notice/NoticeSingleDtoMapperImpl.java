package kr.co.theplay.service.notice;

import javax.annotation.Generated;
import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.dto.notice.NoticeSingleDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-21T15:01:23+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_191-1-ojdkbuild (Oracle Corporation)"
)
public class NoticeSingleDtoMapperImpl implements NoticeSingleDtoMapper {

    @Override
    public Notice toEntity(NoticeSingleDto arg0) {
        if ( arg0 == null ) {
            return null;
        }

        Long id = null;
        String title = null;
        String content = null;

        id = arg0.getId();
        title = arg0.getTitle();
        content = arg0.getContent();

        Notice notice = new Notice( id, title, content );

        return notice;
    }

    @Override
    public NoticeSingleDto toDto(Notice arg0) {
        if ( arg0 == null ) {
            return null;
        }

        NoticeSingleDto noticeSingleDto = new NoticeSingleDto();

        return noticeSingleDto;
    }
}
