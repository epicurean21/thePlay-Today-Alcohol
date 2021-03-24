package kr.co.theplay.dto.zzz;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TestDto {

    private String string;

    @Builder
    public TestDto(String string){
        this.string = string;
    }
}
