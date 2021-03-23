package kr.co.theplay.dto.zzz;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {

    private List<String> filePaths;

    private String title;

    @Builder
    public PostDto(List<String> filePaths, String title){
        this.filePaths = filePaths;
        this.title = title;
    }
}
