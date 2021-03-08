package kr.co.theplay.service.user;

import kr.co.theplay.domain.user.User;
import kr.co.theplay.dto.user.SignUpDto;
import kr.co.theplay.service.MapStructMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SignUpDtoMapper extends MapStructMapper<SignUpDto, User> {
    SignUpDtoMapper INSTANCE = Mappers.getMapper(SignUpDtoMapper.class);
}
