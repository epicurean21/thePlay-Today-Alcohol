package kr.co.theplay.service.zzz;

import kr.co.theplay.domain.zzz.ZUser;
import kr.co.theplay.domain.zzz.ZUserRepository;
import kr.co.theplay.dto.zzz.ZUserReqDto;
import kr.co.theplay.service.common.ResponseService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ZUserService {

    private final ZUserRepository zUserRepository;

    @Transactional
    public void saveZUser(ZUserReqDto zUserReqDto) {
        ZUser zUser = ZUserReqDtoMapper.INSTANCE.toEntity(zUserReqDto);
        zUserRepository.save(zUser);
    }
}
