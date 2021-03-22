package kr.co.theplay.domain.images;

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

    @Builder
    public PostTest(Long id, String title, String thumbnailPath){
        this.id = id;
        this.title = title;
        this.thumbnailPath = thumbnailPath;
    }
}
