package kr.co.theplay.service.user;

import kr.co.theplay.domain.user.User;
import kr.co.theplay.dto.user.UserInfoDto;
import kr.co.theplay.service.MapStructMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserInfoDtoMapper extends MapStructMapper<UserInfoDto, User> {
    UserInfoDtoMapper INSTANCE = Mappers.getMapper(UserInfoDtoMapper.class);
}
