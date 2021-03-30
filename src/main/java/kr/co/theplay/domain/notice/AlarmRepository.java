package kr.co.theplay.domain.notice;

import kr.co.theplay.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUserOrderByCreatedDateDesc(@Param("user") User user);
}
