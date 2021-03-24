package kr.co.theplay.domain.post;

import kr.co.theplay.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    private Integer number;

    @Column
    private String filePath;

    @Builder
    public PostImage(Long id, Post post, Integer number, String filePath){
        this.id = id;
        this.post = post;
        this.number = number;
        this.filePath = filePath;
    }
}
