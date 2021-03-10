package kr.co.theplay.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSendEmailDto {
    private String email;
    private String title;
    private String message;

    @Builder
    public UserSendEmailDto(String email, String title, String message) {
        this.email = email;
        this.title = title;
        this.message = message;
    }
}
