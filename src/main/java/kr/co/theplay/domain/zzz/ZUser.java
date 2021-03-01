package kr.co.theplay.domain.zzz;

import kr.co.theplay.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)// JPA에서 프록시를 생성하려면 반드시 기본 생성자를 가져야함.
public class ZUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String uid;

    @Column
    private String phoneNumber;

    @Column
    private String sex;

    @Builder
    public ZUser(Long id, String name, String uid, String phoneNumber, String sex){
        this.id = id;
        this.name = name;
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
    }

    public void updateUser(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
