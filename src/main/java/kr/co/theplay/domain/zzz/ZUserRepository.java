package kr.co.theplay.domain.zzz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZUserRepository extends JpaRepository<ZUser, Long> {

    Optional<ZUser> findById(Long id);
}
