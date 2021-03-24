package kr.co.theplay.domain.zzz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostTestRepository extends JpaRepository<PostTest, Long> {

    Optional<PostTest> findById(Long id);
}
