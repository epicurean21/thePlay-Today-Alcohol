package kr.co.theplay.domain.notice;

import kr.co.theplay.domain.BaseTimeEntity;
import kr.co.theplay.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_send_id")
    private User userSend;

    @Column
    private String readYn;

    @Column
    private String type;

    @Column
    private String content;

    @Builder
    public Alarm(Long id, User user, User userSend, String readYn, String type, String content) {
        this.id = id;
        this.user = user;
        this.userSend = userSend;
        this.readYn = readYn;
        this.type = type;
        this.content = content;
    }

    public void changeReadYn(String readYn) {
        this.readYn = readYn;
    }
}
