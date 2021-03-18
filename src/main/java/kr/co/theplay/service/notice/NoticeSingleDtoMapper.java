package kr.co.theplay.service.notice;

import kr.co.theplay.domain.notice.Notice;
import kr.co.theplay.dto.notice.NoticeSingleDto;
import kr.co.theplay.service.MapStructMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoticeSingleDtoMapper extends MapStructMapper<NoticeSingleDto, Notice> {
    NoticeSingleDtoMapper INSTANCE = Mappers.getMapper(NoticeSingleDtoMapper.class);

}
