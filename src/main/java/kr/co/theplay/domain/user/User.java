package kr.co.theplay.domain.user;

import kr.co.theplay.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Getter
@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column
    private String privacyYn;

    @Column
    private String newAlarmYn;

    @Builder
    public User(Long id, String email, String password, String nickname,
                String privacyYn, String newAlarmYn) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.privacyYn = privacyYn;
        this.newAlarmYn = newAlarmYn;
    }

    public void changeNewAlarmYn(String yn){
        this.newAlarmYn = yn;
    }

    public String changePrivacyYn(){
        if(privacyYn.equals("N")){
            this.privacyYn = "Y";
            return this.privacyYn;
        }else if(privacyYn.equals("Y")){
            this.privacyYn = "N";
            return this.privacyYn;
        }
        return this.privacyYn;
    }

    public void updateUserPassword(String password) {
        this.password = password;
    }

    public void updateUserNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
