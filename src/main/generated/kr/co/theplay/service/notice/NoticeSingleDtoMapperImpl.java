package kr.co.theplay.service.notice;

import javax.annotation.Generated;
import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.dto.notice.NoticeSingleDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-28T17:04:21+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 15.0.1 (AdoptOpenJDK)"
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
