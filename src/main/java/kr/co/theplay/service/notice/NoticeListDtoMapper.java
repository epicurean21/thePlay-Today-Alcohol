package kr.co.theplay.service.notice;

import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.dto.notice.NoticeListDto;
import kr.co.theplay.service.MapStructMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoticeListDtoMapper extends MapStructMapper<NoticeListDto, Notice> {
    NoticeListDtoMapper INSTANCE = Mappers.getMapper(NoticeListDtoMapper.class);
}
