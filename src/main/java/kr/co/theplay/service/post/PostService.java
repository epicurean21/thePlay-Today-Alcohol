package kr.co.theplay.service.post;

import kr.co.theplay.domain.post.*;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.Post.*;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import kr.co.theplay.service.zzz.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final CommentLikeRepository commentLikeRepository;

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

    public Page<PostResDto> getPostsForMain(String email, int number, int size) {

        // 현재 토큰의 회원 정보가 필요해서 email을 추가로 받아옵니다.

        Pageable pageable = PageRequest.of(number, size);
        Page<Post> posts = postRepository.getLatestPostsForMain(pageable);
        List<Post> postList = posts.getContent();
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        for (int i = 0; i < dtos.size(); i++) {
            //image 매칭
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag 매칭
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            // 게시물 좋아요 여부 확인
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setPostLikeYn("Y");
            else
                dtos.get(i).setPostLikeYn("N");

            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //레시피가 있는 경우 검색
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {
                //ingredient 검색 후 매칭
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step 검색 후 매칭
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);

            }
        }

        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    @Transactional
    public Page<PostResDto> getUserPosts(String email, int number, int size) {

        // Service에서 pageNumber와 size로 pageRequest를 생성 Pageable로 ? PageRequest는 Pageable의 구현채
        Pageable pageable = PageRequest.of(number, size);

        // Page 형식으로 해당 유저의 최신 게시물 들을 가져온다 ! Page형식으로 가져오면, total 개수, 각 contents, pageable 속성을 알 수 있다.
        Page<Post> posts = postRepository.getUserLastestPosts(email, pageable);

        // 각 posts (contents 개수 만큼)을 하나하나 List 형식으로 가져온다. 즉, Contents만 가져온다. .toList() 도 가능
        List<Post> postList = posts.getContent();

        // return 할 dto 형식 PostResDto로 Mapping ? 한다.
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        // List 안에 dto의 개수만큼 for 문
        for (int i = 0; i < dtos.size(); i++) {
            //image 매칭
            // i번 째 postList 안에 이미지들을 가져온다.
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag 매칭
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            // 게시물 좋아요 여부
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setPostLikeYn("Y");
            else
                dtos.get(i).setPostLikeYn("N");

            //레시피가 있는 경우 검색
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {

                //ingredient 검색 후 매칭
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step 검색 후 매칭
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            }
        }
        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    public List<PostCommentDto> getComments(String email, Long postId) {

        if (!postRepository.existsById(postId)) {
            throw new CommonNotFoundException("postNotFound");
        }

        if (!postCommentRepository.existsByPostId(postId)) {
            throw new CommonNotFoundException("commentNotFound");
        }

        List<PostComment> comments = postCommentRepository.findCommentsByPostId(postId);
        List<PostCommentDto> postCommentDtos = comments.stream().map(PostCommentDto::new).collect(Collectors.toList());

        for (int i = 0; i < postCommentDtos.size(); i++) {
            // 대댓글이 존재 한다면
            if (postCommentRepository.existsByPostCommentParentId(postCommentDtos.get(i).getPostCommentId())) {
                List<PostComment> secondComments = postCommentRepository
                        .findSecondCommentsByCommentId(
                                postId, postCommentDtos.get(i).getPostCommentId());

                List<PostSecondCommentDto> secondCommentDtos = secondComments.stream().map(PostSecondCommentDto::new).collect(Collectors.toList());

                // 대댓글 좋아요 여부 및 개수
                for (int j = 0; j < secondCommentDtos.size(); j++) {
                    if (commentLikeRepository.existsByPostCommentIdAndUserEmail(secondCommentDtos.get(j).getPostCommentId(), email)) {
                        secondCommentDtos.get(j).setCommentLikeYn("Y");
                    } else {
                        secondCommentDtos.get(j).setCommentLikeYn("N");
                    }

                    Long likeCount = commentLikeRepository.countAllByPostCommentId(secondCommentDtos.get(j).getPostCommentId());
                    secondCommentDtos.get(j).setCommentLikeCount(likeCount);
                }

                postCommentDtos.get(i).setSecondComments(secondCommentDtos);
            } else { // 대댓글 존재 X
                postCommentDtos.get(i).setSecondComments(new ArrayList<>());
            }

            // 댓글 좋아요 여부 및 개수
            if (commentLikeRepository.existsByPostCommentIdAndUserEmail(postCommentDtos.get(i).getPostCommentId(), email)) {
                postCommentDtos.get(i).setCommentLikeYn("Y");
            } else {
                postCommentDtos.get(i).setCommentLikeYn("N");
            }
            Long likeCount = commentLikeRepository.countAllByPostCommentId(postCommentDtos.get(i).getPostCommentId());
            postCommentDtos.get(i).setCommentLikeCount(likeCount);
        }
        return postCommentDtos;
    }

    @Transactional
    public void updatePost(String email, Long postId, PostReqDto postReqDto) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new CommonNotFoundException("postNotFound"));

        //로그인한 사용자가 작성한 글이 맞는 지 확인 후 예외처리
        if (!post.getUser().getEmail().equals(email)) {
            throw new CommonBadRequestException("accessException");
        }

        //post 내용 update
        post.updatePost(postReqDto.getContent(), postReqDto.getHaveRecipeYn());

        //기존 내용들 삭제
        deleteForUpdate(post);

        //AlcoholTag list 생성, 각각에 post 세팅, 저장
        if (postReqDto.getAlcoholTags() != null && postReqDto.getAlcoholTags().size() > 0) {
            List<AlcoholTag> updatedAlcoholTags = postReqDto.getAlcoholTags().stream().map(AlcoholTagDto::toEntity).collect(Collectors.toList());
            updatedAlcoholTags.forEach(e -> e.changePost(post));
            alcoholTagRepository.saveAll(updatedAlcoholTags);
        }

        //RecipeIngredient list 생성, 각각에 post 세팅, 저장
        if (postReqDto.getIngredients() != null && postReqDto.getIngredients().size() > 0) {
            List<RecipeIngredient> updatedIngredients = postReqDto.getIngredients().stream().map(RecipeIngredientDto::toEntity).collect(Collectors.toList());
            updatedIngredients.forEach(e -> e.changePost(post));
            recipeIngredientRepository.saveAll(updatedIngredients);
        }

        //RecipeStep list 생성, 각 post 세팅, 저장
        if (postReqDto.getSteps() != null && postReqDto.getSteps().size() > 0) {
            List<RecipeStep> updatedSteps = postReqDto.getSteps().stream().map(RecipeStepDto::toEntity).collect(Collectors.toList());
            updatedSteps.forEach(e -> e.changePost(post));
            recipeStepRepository.saveAll(updatedSteps);
        }

    }

    //flush를 통해 삭제 쿼리를 먼저 실행하도록 함.
    public void deleteForUpdate(Post post) {
        //alcoholTags, ingredients, steps는 갯수도 변경될 수 있으므로 데이터 삭제 후 삽입으로 update 구현
        List<AlcoholTag> alcoholTags = alcoholTagRepository.findByPost(post);
        List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(post.getId());
        List<RecipeStep> steps = recipeStepRepository.findByPostId(post.getId());

        //TODO : 쿼리실행이 하나씩 되고 있음. 성능 개선 필요.
        alcoholTagRepository.deleteAll(alcoholTags);
        recipeIngredientRepository.deleteAll(ingredients);
        recipeStepRepository.deleteAll(steps);

        alcoholTagRepository.flush();
        recipeIngredientRepository.flush();
        recipeStepRepository.flush();
    }

    @Transactional
    public void createComment(String email, Long postId, PostCommentReqDto postCommentReqDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new CommonNotFoundException("postNotFound"));

        // 부모 댓글 존재하지 않을때
        // 대댓글에 대댓글을 달려고 할 때
        // 부모 댓글과 대댓글의 게시물 id가 다를 때
        if(postCommentReqDto.getPostCommentParentId() != 0) {
            PostComment parentComment = postCommentRepository
                    .findById(postCommentReqDto.getPostCommentParentId())
                    .orElseThrow(() -> new CommonNotFoundException("parentCommentNotFound"));

            if(parentComment.getPostCommentParentId() != 0)
                throw new CommonBadRequestException("commentNotAllowed");

            if(parentComment.getPost().getId() != postId)
                throw new CommonBadRequestException("commentPostNotEqual");
        }

        PostComment postComment = PostComment.builder()
                .post(post)
                .user(user)
                .postCommentParentId(postCommentReqDto.getPostCommentParentId())
                .content(postCommentReqDto.getContent())
                .build();
        postCommentRepository.save(postComment);
    }
}
