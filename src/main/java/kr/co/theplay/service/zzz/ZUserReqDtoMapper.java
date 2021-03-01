package kr.co.theplay.service.zzz;

import kr.co.theplay.domain.zzz.ZUser;
import kr.co.theplay.dto.zzz.ZUserReqDto;
import kr.co.theplay.service.MapStructMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ZUserReqDtoMapper extends MapStructMapper<ZUserReqDto, ZUser> {
    ZUserReqDtoMapper INSTANCE = Mappers.getMapper(ZUserReqDtoMapper.class);
}
