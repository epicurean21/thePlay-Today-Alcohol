package kr.co.theplay.service.user;

import javax.annotation.Generated;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRole;
import kr.co.theplay.dto.user.UserInfoDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-15T19:18:25+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 15.0.1 (AdoptOpenJDK)"
)
public class UserInfoDtoMapperImpl implements UserInfoDtoMapper {

    @Override
    public User toEntity(UserInfoDto dto) {
        if ( dto == null ) {
            return null;
        }

        Long id = null;
        String email = null;
        String nickname = null;

        id = dto.getId();
        email = dto.getEmail();
        nickname = dto.getNickname();

        String password = null;
        String privacyYn = null;
        UserRole userRole = null;

        User user = new User( id, email, password, nickname, privacyYn, userRole );

        return user;
    }

    @Override
    public UserInfoDto toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserInfoDto userInfoDto = new UserInfoDto();

        return userInfoDto;
    }
}
