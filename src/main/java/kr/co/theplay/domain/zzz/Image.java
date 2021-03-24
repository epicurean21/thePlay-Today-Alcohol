package kr.co.theplay.domain.zzz;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long postTestId;

    @Column(columnDefinition = "TEXT")
    private String filePath;

    @Builder
    public Image(Long id, Long postTestId, String filePath) {
        this.id = id;
        this.postTestId = postTestId;
        this.filePath = filePath;
    }

    public void changeFilePath(String filePath){
        this.filePath = filePath;
    }
}
