package kr.co.theplay.service.notice;

import javax.annotation.Generated;
import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.domain.notice.Notice.NoticeBuilder;
import kr.co.theplay.dto.notice.NoticeSingleDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-18T18:43:03+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 15.0.1 (AdoptOpenJDK)"
)
public class NoticeSingleDtoMapperImpl implements NoticeSingleDtoMapper {

    @Override
    public Notice toEntity(NoticeSingleDto arg0) {
        if ( arg0 == null ) {
            return null;
        }

        NoticeBuilder notice = Notice.builder();

        notice.id( arg0.getId() );
        notice.title( arg0.getTitle() );
        notice.content( arg0.getContent() );

        return notice.build();
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
