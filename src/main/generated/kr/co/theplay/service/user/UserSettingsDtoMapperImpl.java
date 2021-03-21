package kr.co.theplay.service.user;

import javax.annotation.Generated;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.User.UserBuilder;
import kr.co.theplay.dto.user.UserSettingsDto;
import kr.co.theplay.dto.user.UserSettingsDto.UserSettingsDtoBuilder;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-18T16:48:13+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 15.0.1 (AdoptOpenJDK)"
)
public class UserSettingsDtoMapperImpl implements UserSettingsDtoMapper {

    @Override
    public User toEntity(UserSettingsDto arg0) {
        if ( arg0 == null ) {
            return null;
        }

        UserBuilder user = User.builder();

        user.email( arg0.getEmail() );
        user.nickname( arg0.getNickname() );

        return user.build();
    }

    @Override
    public UserSettingsDto toDto(User arg0) {
        if ( arg0 == null ) {
            return null;
        }

        UserSettingsDtoBuilder userSettingsDto = UserSettingsDto.builder();

        userSettingsDto.nickname( arg0.getNickname() );
        userSettingsDto.email( arg0.getEmail() );

        return userSettingsDto.build();
    }
}
