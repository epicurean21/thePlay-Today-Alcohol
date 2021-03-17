package kr.co.theplay.service.zzz;

import javax.annotation.Generated;
import kr.co.theplay.domain.zzz.ZUser;
import kr.co.theplay.dto.zzz.ZUserReqDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-17T23:07:39+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_191-1-ojdkbuild (Oracle Corporation)"
)
public class ZUserReqDtoMapperImpl implements ZUserReqDtoMapper {

    @Override
    public ZUser toEntity(ZUserReqDto dto) {
        if ( dto == null ) {
            return null;
        }

        String name = null;
        String uid = null;
        String phoneNumber = null;
        String sex = null;

        name = dto.getName();
        uid = dto.getUid();
        phoneNumber = dto.getPhoneNumber();
        sex = dto.getSex();

        Long id = null;

        ZUser zUser = new ZUser( id, name, uid, phoneNumber, sex );

        return zUser;
    }

    @Override
    public ZUserReqDto toDto(ZUser entity) {
        if ( entity == null ) {
            return null;
        }

        ZUserReqDto zUserReqDto = new ZUserReqDto();

        return zUserReqDto;
    }
}
