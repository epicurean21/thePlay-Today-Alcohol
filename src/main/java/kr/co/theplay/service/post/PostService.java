package kr.co.theplay.service.post;

import kr.co.theplay.domain.post.*;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.Post.*;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import kr.co.theplay.service.zzz.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AlcoholTagRepository alcoholTagRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;
    private final PostReportRepository postReportRepository;

    @Transactional
    public void create(String email, PostReqDto postReqDto, List<MultipartFile> files) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        // Validation <1> 해당 사용자가 신고를 5회이상 받았다 그럼 작성 불가로 Exception
        int cnt = postReportRepository.findCountReportByUser(user.getEmail());
        if (cnt >= 5) { // 5회 이상 신고를 받았다
            throw new CommonBadRequestException("postUserReportExceed");
        }

        //Post 생성, post의 user 세팅, 저장
        Post post = postReqDto.toEntity();
        post.changeUser(user);
        postRepository.save(post);

        //AlcoholTag list 생성, 각 alcoholTag의 post 세팅, 저장
        if (postReqDto.getAlcoholTags() != null && postReqDto.getAlcoholTags().size() > 0) {
            List<AlcoholTag> alcoholTags = postReqDto.getAlcoholTags().stream().map(AlcoholTagDto::toEntity).collect(Collectors.toList());
            alcoholTags.forEach(e -> e.changePost(post));
            alcoholTagRepository.saveAll(alcoholTags);
        }

        //RecipeIngredient list 생성, 각 post 세팅, 저장
        if (postReqDto.getIngredients() != null && postReqDto.getIngredients().size() > 0) {
            List<RecipeIngredient> ingredients = postReqDto.getIngredients().stream().map(RecipeIngredientDto::toEntity).collect(Collectors.toList());
            ingredients.forEach(e -> e.changePost(post));
            recipeIngredientRepository.saveAll(ingredients);
        }

        //RecipeStep list 생성, 각 post 세팅, 저장
        if (postReqDto.getSteps() != null && postReqDto.getSteps().size() > 0) {
            List<RecipeStep> steps = postReqDto.getSteps().stream().map(RecipeStepDto::toEntity).collect(Collectors.toList());
            steps.forEach(e -> e.changePost(post));
            recipeStepRepository.saveAll(steps);
        }

        //file 저장
        for (int i = 0; i < files.size(); i++) {

            String filePath = null;
            try {
                filePath = s3Service.upload(files.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (filePath == "EXCEED") {
                throw new CommonBadRequestException("imageSizeExcessLimit");
            }
            PostImage postImage = PostImage.builder().post(post).number(i).filePath(filePath).build();
            postImageRepository.save(postImage);
        }

    }

    @Transactional
    public void reportPost(String email, PostReportReqDto postReportReqDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        Post post = postRepository.findById(postReportReqDto.getPostId()).orElseThrow(() -> new CommonNotFoundException("postNotFound"));

        if (postReportRepository.findByUserAndPost(user, post).isPresent()) {
            throw new CommonBadRequestException("postReportConflict");
        }

        int reason = Integer.parseInt(postReportReqDto.getContent());
        if (reason > 2 || reason < 0) {
            throw new CommonBadRequestException("postReportReasonIncorrect");
        }

        PostReport postReport = PostReport.builder().post(post).user(user).content(postReportReqDto.getContent()).build();
        postReportRepository.save(postReport);
    }
}
