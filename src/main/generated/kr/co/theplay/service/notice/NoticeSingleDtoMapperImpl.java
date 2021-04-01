package kr.co.theplay.service.notice;

import javax.annotation.Generated;
import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.dto.notice.NoticeSingleDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-04-01T19:06:47+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_191-1-ojdkbuild (Oracle Corporation)"
)
public class NoticeSingleDtoMapperImpl implements NoticeSingleDtoMapper {

    @Override
    public Notice toEntity(NoticeSingleDto dto) {
        if ( dto == null ) {
            return null;
        }

        Long id = null;
        String title = null;
        String content = null;

        id = dto.getId();
        title = dto.getTitle();
        content = dto.getContent();

        Notice notice = new Notice( id, title, content );

        return notice;
    }

    @Override
    public NoticeSingleDto toDto(Notice entity) {
        if ( entity == null ) {
            return null;
        }

        NoticeSingleDto noticeSingleDto = new NoticeSingleDto();

        return noticeSingleDto;
    }
}
