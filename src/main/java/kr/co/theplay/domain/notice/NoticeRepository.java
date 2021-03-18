package kr.co.theplay.domain.notice;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    /*
    @Query("select n.id, n.title from Notice n order by n.createdDate desc ")
    List<Notice> findAllOrderByCreatedDateDesc();
    */

    List<Notice> findAllByOrderByCreatedDateDesc();
}
