package kr.co.theplay.domain.zzz;

import com.sun.xml.bind.v2.model.core.ReferencePropertyInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PostTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String thumbnailPath;

    @Column
    private Integer num;

    @Builder
    public PostTest(Long id, String title, String thumbnailPath, Integer num){
        this.id = id;
        this.title = title;
        this.thumbnailPath = thumbnailPath;
        this.num = num;
    }
}
