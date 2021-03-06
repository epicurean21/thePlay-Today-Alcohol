package kr.co.theplay.service.post;

import kr.co.theplay.domain.notice.Alarm;
import kr.co.theplay.domain.notice.AlarmRepository;
import kr.co.theplay.domain.post.*;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.domain.user.UserRecipe;
import kr.co.theplay.domain.user.UserRecipeRepository;
import kr.co.theplay.domain.user.UserRepository;
import kr.co.theplay.dto.post.*;
import kr.co.theplay.dto.recipe.RecipeSaveResDto;
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
    private final UserRecipeRepository userRecipeRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public void create(String email, PostReqDto postReqDto, List<MultipartFile> files) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        // Validation <1> ?????? ???????????? ????????? 5????????? ????????? ?????? ?????? ????????? Exception
        int cnt = postReportRepository.findCountReportByUser(user.getEmail());
        if (cnt >= 5) { // 5??? ?????? ????????? ?????????
            throw new CommonBadRequestException("postUserReportExceed");
        }

        //Post ??????, post??? user ??????, ??????
        Post post = postReqDto.toEntity();
        post.changeUser(user);
        postRepository.save(post);

        //AlcoholTag list ??????, ??? alcoholTag??? post ??????, ??????
        if (postReqDto.getAlcoholTags() != null && postReqDto.getAlcoholTags().size() > 0) {
            List<AlcoholTag> alcoholTags = postReqDto.getAlcoholTags().stream().map(AlcoholTagDto::toEntity).collect(Collectors.toList());
            alcoholTags.forEach(e -> e.changePost(post));
            alcoholTagRepository.saveAll(alcoholTags);
        }

        //RecipeIngredient list ??????, ??? post ??????, ??????
        if (postReqDto.getIngredients() != null && postReqDto.getIngredients().size() > 0) {
            List<RecipeIngredient> ingredients = postReqDto.getIngredients().stream().map(RecipeIngredientDto::toEntity).collect(Collectors.toList());
            ingredients.forEach(e -> e.changePost(post));
            recipeIngredientRepository.saveAll(ingredients);
        }

        //RecipeStep list ??????, ??? post ??????, ??????
        if (postReqDto.getSteps() != null && postReqDto.getSteps().size() > 0) {
            List<RecipeStep> steps = postReqDto.getSteps().stream().map(RecipeStepDto::toEntity).collect(Collectors.toList());
            steps.forEach(e -> e.changePost(post));
            recipeStepRepository.saveAll(steps);
        }

        //file ??????
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

        // ?????? ????????? ?????? ????????? ???????????? email??? ????????? ???????????????.

        Pageable pageable = PageRequest.of(number, size);
        Page<Post> posts = postRepository.getLatestPostsForMain(pageable);
        List<Post> postList = posts.getContent();
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        for (int i = 0; i < dtos.size(); i++) {
            //image ??????
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag ??????
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            // ????????? ????????? ?????? ??????
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setPostLikeYn("Y");
            else
                dtos.get(i).setPostLikeYn("N");

            // ????????? ?????? ??????
            Integer postLikeCnt = postLikeRepository.countPostLikeByPostId(dtos.get(i).getPostId());
            dtos.get(i).setPostLikeCnt(postLikeCnt);

            // ???????????? ????????? ?????? ??????, ???????????? ??????????????? ?????? ?????????
            if (dtos.get(i).getHaveRecipeYn().equals("Y") &&
                    userRecipeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setSaveRecipeYn("Y");
            else
                dtos.get(i).setSaveRecipeYn("N");

            //?????? ?????? ??????
            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //?????? ?????? ????????? ??? ?????? ??????
            if (commentCnt != 0) {
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(postList.get(i).getId(), (long) 0);
                dtos.get(i).setCommentNickname(comment.getUser().getNickname());
                dtos.get(i).setComment(comment.getContent());
            } else {
                dtos.get(i).setComment("N");
                dtos.get(i).setCommentNickname("N");
            }

            //???????????? ?????? ?????? ??????
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {
                //ingredient ?????? ??? ??????
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step ?????? ??? ??????
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            } else {
                dtos.get(i).setIngredients(new ArrayList<>());
                dtos.get(i).setSteps(new ArrayList<>());
            }
        }

        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    @Transactional
    public Page<PostResDto> getUserPosts(String email, int number, int size) {

        // Service?????? pageNumber??? size??? pageRequest??? ?????? Pageable??? ? PageRequest??? Pageable??? ?????????
        Pageable pageable = PageRequest.of(number, size);

        // Page ???????????? ?????? ????????? ?????? ????????? ?????? ???????????? ! Page???????????? ????????????, total ??????, ??? contents, pageable ????????? ??? ??? ??????.
        Page<Post> posts = postRepository.getUserLastestPosts(email, pageable);

        // ??? posts (contents ?????? ??????)??? ???????????? List ???????????? ????????????. ???, Contents??? ????????????. .toList() ??? ??????
        List<Post> postList = posts.getContent();

        // return ??? dto ?????? PostResDto??? Mapping ? ??????.
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        // List ?????? dto??? ???????????? for ???
        for (int i = 0; i < dtos.size(); i++) {
            //image ??????
            // i??? ??? postList ?????? ??????????????? ????????????.
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag ??????
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            // ????????? ????????? ??????
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setPostLikeYn("Y");
            else
                dtos.get(i).setPostLikeYn("N");

            Integer postLikeCnt = postLikeRepository.countPostLikeByPostId(dtos.get(i).getPostId());
            dtos.get(i).setPostLikeCnt(postLikeCnt);

            // ???????????? ????????? ?????? ??????, ???????????? ??????????????? ?????? ?????????
            if (dtos.get(i).getHaveRecipeYn().equals("Y") &&
                    userRecipeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setSaveRecipeYn("Y");
            else
                dtos.get(i).setSaveRecipeYn("N");

            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //?????? ?????? ????????? ??? ?????? ??????
            if (commentCnt != 0) {
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(postList.get(i).getId(), (long) 0);
                dtos.get(i).setCommentNickname(comment.getUser().getNickname());
                dtos.get(i).setComment(comment.getContent());
            } else {
                dtos.get(i).setComment("N");
                dtos.get(i).setCommentNickname("N");
            }

            //???????????? ?????? ?????? ??????
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {

                //ingredient ?????? ??? ??????
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step ?????? ??? ??????
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            } else {
                dtos.get(i).setIngredients(new ArrayList<>());
                dtos.get(i).setSteps(new ArrayList<>());
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
            // ???????????? ?????? ?????????
            if (postCommentRepository.existsByPostCommentParentId(postCommentDtos.get(i).getPostCommentId())) {
                List<PostComment> secondComments = postCommentRepository
                        .findSecondCommentsByCommentId(
                                postId, postCommentDtos.get(i).getPostCommentId());

                List<PostSecondCommentDto> secondCommentDtos = secondComments.stream().map(PostSecondCommentDto::new).collect(Collectors.toList());

                // ????????? ????????? ?????? ??? ??????
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
            } else { // ????????? ?????? X
                postCommentDtos.get(i).setSecondComments(new ArrayList<>());
            }

            // ?????? ????????? ?????? ??? ??????
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

        //???????????? ???????????? ????????? ?????? ?????? ??? ?????? ??? ????????????
        if (!post.getUser().getEmail().equals(email)) {
            throw new CommonBadRequestException("accessException");
        }

        //post ?????? update
        post.updatePost(postReqDto.getContent(), postReqDto.getHaveRecipeYn());

        //?????? ????????? ??????
        deleteForUpdate(post);

        //AlcoholTag list ??????, ????????? post ??????, ??????
        if (postReqDto.getAlcoholTags() != null && postReqDto.getAlcoholTags().size() > 0) {
            List<AlcoholTag> updatedAlcoholTags = postReqDto.getAlcoholTags().stream().map(AlcoholTagDto::toEntity).collect(Collectors.toList());
            updatedAlcoholTags.forEach(e -> e.changePost(post));
            alcoholTagRepository.saveAll(updatedAlcoholTags);
        }

        //RecipeIngredient list ??????, ????????? post ??????, ??????
        if (postReqDto.getIngredients() != null && postReqDto.getIngredients().size() > 0) {
            List<RecipeIngredient> updatedIngredients = postReqDto.getIngredients().stream().map(RecipeIngredientDto::toEntity).collect(Collectors.toList());
            updatedIngredients.forEach(e -> e.changePost(post));
            recipeIngredientRepository.saveAll(updatedIngredients);
        }

        //RecipeStep list ??????, ??? post ??????, ??????
        if (postReqDto.getSteps() != null && postReqDto.getSteps().size() > 0) {
            List<RecipeStep> updatedSteps = postReqDto.getSteps().stream().map(RecipeStepDto::toEntity).collect(Collectors.toList());
            updatedSteps.forEach(e -> e.changePost(post));
            recipeStepRepository.saveAll(updatedSteps);
        }

    }

    //flush??? ?????? ?????? ????????? ?????? ??????????????? ???.
    public void deleteForUpdate(Post post) {
        //alcoholTags, ingredients, steps??? ????????? ????????? ??? ???????????? ????????? ?????? ??? ???????????? update ??????
        List<AlcoholTag> alcoholTags = alcoholTagRepository.findByPost(post);
        List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(post.getId());
        List<RecipeStep> steps = recipeStepRepository.findByPostId(post.getId());

        //TODO : ??????????????? ????????? ?????? ??????. ?????? ?????? ??????.
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

        // ?????? ?????? ???????????? ?????????
        // ???????????? ???????????? ????????? ??? ???
        // ?????? ????????? ???????????? ????????? id??? ?????? ???
        if (postCommentReqDto.getPostCommentParentId() != 0) {
            PostComment parentComment = postCommentRepository
                    .findById(postCommentReqDto.getPostCommentParentId())
                    .orElseThrow(() -> new CommonNotFoundException("parentCommentNotFound"));

            if (parentComment.getPostCommentParentId() != 0)
                throw new CommonBadRequestException("commentNotAllowed");

            if (parentComment.getPost().getId() != postId)
                throw new CommonBadRequestException("commentPostNotEqual");
        }

        PostComment postComment = PostComment.builder()
                .post(post)
                .user(user)
                .postCommentParentId(postCommentReqDto.getPostCommentParentId())
                .content(postCommentReqDto.getContent())
                .build();
        postCommentRepository.save(postComment); // ?????? ??????

        // ?????? ??????
        // ?????????, user (?????? ?????????), dto
        uploadCommentAlarm(post, user, postCommentReqDto);
    }

    public Page<PostResDto> getFollowingPosts(String email, int number, int size) {

        Pageable pageable = PageRequest.of(number, size);
        Page<Post> posts = postRepository.getFollowingPosts(pageable, email);
        List<Post> postList = posts.getContent();
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        for (int i = 0; i < dtos.size(); i++) {

            //image ??????
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag ??????
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            //????????? ??????????????? ??????
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent()) {
                dtos.get(i).setPostLikeYn("Y");
            } else {
                dtos.get(i).setPostLikeYn("N");
            }

            Integer postLikeCnt = postLikeRepository.countPostLikeByPostId(dtos.get(i).getPostId());
            dtos.get(i).setPostLikeCnt(postLikeCnt);

            // ???????????? ????????? ?????? ??????, ???????????? ??????????????? ?????? ?????????
            if (dtos.get(i).getHaveRecipeYn().equals("Y") &&
                    userRecipeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setSaveRecipeYn("Y");
            else
                dtos.get(i).setSaveRecipeYn("N");

            //?????? ??? ??????
            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //?????? ?????? ????????? ??? ?????? ??????
            if (commentCnt != 0) {
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(postList.get(i).getId(), (long) 0);
                dtos.get(i).setCommentNickname(comment.getUser().getNickname());
                dtos.get(i).setComment(comment.getContent());
            } else {
                dtos.get(i).setComment("N");
                dtos.get(i).setCommentNickname("N");
            }

            //???????????? ?????? ?????? ??????
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {
                //ingredient ?????? ??? ??????
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step ?????? ??? ??????
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            } else {
                dtos.get(i).setIngredients(new ArrayList<>());
                dtos.get(i).setSteps(new ArrayList<>());
            }
        }

        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    @Transactional
    public RecipeSaveResDto changeSaveRecipe(String email, Long alcoholTagId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        AlcoholTag alcoholTag = alcoholTagRepository.findById(alcoholTagId).orElseThrow(() -> new CommonNotFoundException("alcoholTagNotFound"));

        if (alcoholTag.getRecipeYn().equals("N")) {
            throw new CommonBadRequestException("alcoholTagNotRecipe");
        }

        RecipeSaveResDto recipeSaveResDto = new RecipeSaveResDto();
        // ?????? ?????? ??? ?????? (????????????) ??? ???????????? ???????????????
        if (!userRecipeRepository.existsByAlcoholTagAndUser(alcoholTag, user)) {

            // ????????? ?????? ??????.
            UserRecipe userRecipe = UserRecipe.builder()
                    .alcoholTag(alcoholTag)
                    .user(user)
                    .build();
            recipeSaveResDto = RecipeSaveResDto.builder().saveYn("Y").build();
            userRecipeRepository.save(userRecipe);
        } else {
            // ?????? ????????? ?????????????????? ????????????
            UserRecipe userRecipe = userRecipeRepository.findByAlcoholTagAndUser(alcoholTag, user);
            recipeSaveResDto = RecipeSaveResDto.builder().saveYn("N").build();
            userRecipeRepository.delete(userRecipe);
        }
        return recipeSaveResDto;
    }

    @Transactional
    public PostLikeChangeResDto changeLikePost(String email, Long postId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new CommonNotFoundException("postNotFound"));
        PostLikeChangeResDto postLikeChangeResDto = new PostLikeChangeResDto();
        // ?????? ?????? ???????????? ???????????? ????????? ????????????
        if (!postLikeRepository.existsByPostAndUser(post, user)) {

            // ????????? ???????????? ?????????
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeChangeResDto = PostLikeChangeResDto.builder().likeYn("Y").build();

            // alarm ??????, (????????? ??????, ????????? ????????????)
            uploadLikeAlarm(post, user);

            postLikeRepository.save(postLike);
        } else {
            // ?????? ????????? ?????????????????? ????????????
            PostLike postLike = postLikeRepository.findByPostAndUser(post, user);
            postLikeRepository.delete(postLike);
            postLikeChangeResDto = PostLikeChangeResDto.builder().likeYn("N").build();
        }

        return postLikeChangeResDto;
    }

    public Page<PostResDto> getUserLikedPosts(String email, int number, int size) {

        Pageable pageable = PageRequest.of(number, size);

        // ????????? ?????? ???????????? ??????????????? ????????????, Paging ??????
        Page<Post> posts = postLikeRepository.findPostLikeByUserEmail(pageable, email);
        List<Post> postList = posts.getContent();
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        for (int i = 0; i < dtos.size(); i++) {
            //image ??????
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag ??????
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            //????????? ??????????????? ??????
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent()) {
                dtos.get(i).setPostLikeYn("Y");
            } else {
                dtos.get(i).setPostLikeYn("N");
            }

            // ???????????? ????????? ?????? ??????, ???????????? ??????????????? ?????? ?????????
            if (dtos.get(i).getHaveRecipeYn().equals("Y") &&
                    userRecipeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setSaveRecipeYn("Y");
            else
                dtos.get(i).setSaveRecipeYn("N");

            Integer postLikeCnt = postLikeRepository.countPostLikeByPostId(dtos.get(i).getPostId());
            dtos.get(i).setPostLikeCnt(postLikeCnt);

            //?????? ??? ??????
            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //?????? ?????? ????????? ??? ?????? ??????
            if (commentCnt != 0) {
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(postList.get(i).getId(), (long) 0);
                dtos.get(i).setCommentNickname(comment.getUser().getNickname());
                dtos.get(i).setComment(comment.getContent());
            } else {
                dtos.get(i).setComment("N");
                dtos.get(i).setCommentNickname("N");
            }

            //???????????? ?????? ?????? ??????
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {
                //ingredient ?????? ??? ??????
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step ?????? ??? ??????
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            } else {
                dtos.get(i).setIngredients(new ArrayList<>());
                dtos.get(i).setSteps(new ArrayList<>());
            }
        }

        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    public Page<PostResDto> getOtherUserPosts(String email, Long userId, int number, int size) {
        // ?????? ????????? ?????? ??????????????? ???????????? /user/{userId}/posts
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        // Service?????? pageNumber??? size??? pageRequest??? ?????? Pageable??? ? PageRequest??? Pageable??? ?????????
        Pageable pageable = PageRequest.of(number, size);
        Page<Post> posts = postRepository.getUserLastestPosts(user.getEmail(), pageable);
        List<Post> postList = posts.getContent();

        // return ??? dto ?????? PostResDto??? Mapping ? ??????.
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        // List ?????? dto??? ???????????? for ???
        for (int i = 0; i < dtos.size(); i++) {
            //image ??????
            // i??? ??? postList ?????? ??????????????? ????????????.
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag ??????
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            // ????????? ????????? ??????
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setPostLikeYn("Y");
            else
                dtos.get(i).setPostLikeYn("N");

            Integer postLikeCnt = postLikeRepository.countPostLikeByPostId(dtos.get(i).getPostId());
            dtos.get(i).setPostLikeCnt(postLikeCnt);

            // ???????????? ????????? ?????? ??????, ???????????? ??????????????? ?????? ?????????
            if (dtos.get(i).getHaveRecipeYn().equals("Y") &&
                    userRecipeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setSaveRecipeYn("Y");
            else
                dtos.get(i).setSaveRecipeYn("N");

            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //?????? ?????? ????????? ??? ?????? ??????
            if (commentCnt != 0) {
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(postList.get(i).getId(), (long) 0);
                dtos.get(i).setCommentNickname(comment.getUser().getNickname());
                dtos.get(i).setComment(comment.getContent());
            } else {
                dtos.get(i).setComment("N");
                dtos.get(i).setCommentNickname("N");
            }

            //???????????? ?????? ?????? ??????
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {

                //ingredient ?????? ??? ??????
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step ?????? ??? ??????
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            } else {
                dtos.get(i).setIngredients(new ArrayList<>());
                dtos.get(i).setSteps(new ArrayList<>());
            }
        }
        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    public Page<PostResDto> getOtherUsersLikedPosts(String email, Long userId, int number, int size) {
        // ?????? ????????? ?????? ??????????????? ???????????? /user/{userId}/posts
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        // ?????? ????????? ???????????? ?????? ????????? ??????????????? ?????????
        if (user.getPrivacyYn().equals("Y") && !user.getEmail().equals(email)) {
            throw new CommonBadRequestException("userPrivacyInvaded");
        }
        // Service?????? pageNumber??? size??? pageRequest??? ?????? Pageable??? ? PageRequest??? Pageable??? ?????????
        Pageable pageable = PageRequest.of(number, size);

        // ?????? ?????? (user)??? ???????????? ?????? ??????????????? ????????????
        Page<Post> posts = postLikeRepository.findPostLikeByUserEmail(pageable, user.getEmail());
        List<Post> postList = posts.getContent();

        // return ??? dto ?????? PostResDto??? Mapping ? ??????.
        List<PostResDto> dtos = posts.stream().map(PostResDto::new).collect(Collectors.toList());

        // List ?????? dto??? ???????????? for ???
        for (int i = 0; i < dtos.size(); i++) {
            //image ??????
            // i??? ??? postList ?????? ??????????????? ????????????.
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag ??????
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            // ????????? ????????? ??????
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setPostLikeYn("Y");
            else
                dtos.get(i).setPostLikeYn("N");

            Integer postLikeCnt = postLikeRepository.countPostLikeByPostId(dtos.get(i).getPostId());
            dtos.get(i).setPostLikeCnt(postLikeCnt);

            // ???????????? ????????? ?????? ??????, ???????????? ??????????????? ?????? ?????????
            if (dtos.get(i).getHaveRecipeYn().equals("Y") &&
                    userRecipeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setSaveRecipeYn("Y");
            else
                dtos.get(i).setSaveRecipeYn("N");

            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //?????? ?????? ????????? ??? ?????? ??????
            if (commentCnt != 0) {
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(postList.get(i).getId(), (long) 0);
                dtos.get(i).setCommentNickname(comment.getUser().getNickname());
                dtos.get(i).setComment(comment.getContent());
            } else {
                dtos.get(i).setComment("N");
                dtos.get(i).setCommentNickname("N");
            }

            //???????????? ?????? ?????? ??????
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {

                //ingredient ?????? ??? ??????
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step ?????? ??? ??????
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            } else {
                dtos.get(i).setIngredients(new ArrayList<>());
                dtos.get(i).setSteps(new ArrayList<>());
            }
        }
        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    public Page<PostResDto> getSearchPosts(String email, String recipeName, int number, int size) {
        Pageable pageable = PageRequest.of(number, size);

        Page<Post> posts = postRepository.getSearchPosts(recipeName, pageable);
        List<Post> postList = posts.getContent();
        List<PostResDto> dtos = postList.stream().map(PostResDto::new).collect(Collectors.toList());

        for (int i = 0; i < dtos.size(); i++) {
            //image ??????
            List<PostImage> images = postList.get(i).getImages();
            List<PostImageDto> imageDtos = images.stream().map(PostImageDto::new).collect(Collectors.toList());
            dtos.get(i).setImages(imageDtos);

            //alcoholTag ??????
            List<AlcoholTag> alcoholTags = postList.get(i).getAlcoholTags();
            List<AlcoholTagDto> alcoholTagDtos = alcoholTags.stream().map(AlcoholTagDto::new).collect(Collectors.toList());
            dtos.get(i).setAlcoholTags(alcoholTagDtos);

            //????????? ??????????????? ??????
            if (postLikeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent()) {
                dtos.get(i).setPostLikeYn("Y");
            } else {
                dtos.get(i).setPostLikeYn("N");
            }

            // ???????????? ????????? ?????? ??????, ???????????? ??????????????? ?????? ?????????
            if (dtos.get(i).getHaveRecipeYn().equals("Y") &&
                    userRecipeRepository.findByPostIdAndUserEmail(postList.get(i).getId(), email).isPresent())
                dtos.get(i).setSaveRecipeYn("Y");
            else
                dtos.get(i).setSaveRecipeYn("N");

            Integer postLikeCnt = postLikeRepository.countPostLikeByPostId(dtos.get(i).getPostId());
            dtos.get(i).setPostLikeCnt(postLikeCnt);

            //?????? ??? ??????
            Long commentCnt = postCommentRepository.getCountOfPostComment(postList.get(i).getId());
            dtos.get(i).setCommentCnt(commentCnt);

            //?????? ?????? ????????? ??? ?????? ??????
            if (commentCnt != 0) {
                PostComment comment = postCommentRepository.findFirstByPostIdAndPostCommentParentIdOrderByCreatedDateDesc(postList.get(i).getId(), (long) 0);
                dtos.get(i).setCommentNickname(comment.getUser().getNickname());
                dtos.get(i).setComment(comment.getContent());
            } else {
                dtos.get(i).setComment("N");
                dtos.get(i).setCommentNickname("N");
            }

            //???????????? ?????? ?????? ??????
            if (dtos.get(i).getHaveRecipeYn().equals("Y")) {
                //ingredient ?????? ??? ??????
                List<RecipeIngredient> ingredients = recipeIngredientRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeIngredientDto> ingredientDtos = ingredients.stream().map(RecipeIngredientDto::new).collect(Collectors.toList());
                dtos.get(i).setIngredients(ingredientDtos);

                //step ?????? ??? ??????
                List<RecipeStep> steps = recipeStepRepository.findByPostId(dtos.get(i).getPostId());
                List<RecipeStepDto> stepDtos = steps.stream().map(RecipeStepDto::new).collect(Collectors.toList());
                dtos.get(i).setSteps(stepDtos);
            } else {
                dtos.get(i).setIngredients(new ArrayList<>());
                dtos.get(i).setSteps(new ArrayList<>());
            }
        }
        return new PageImpl<>(dtos, pageable, posts.getTotalElements());
    }

    @Transactional
    public void deletePostById(String email, Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new CommonNotFoundException("postNotFound"));
        if (!post.getUser().getEmail().equals(email)) {
            throw new CommonBadRequestException("accessException");
        }

        List<PostImage> images = post.getImages();
        images.forEach(e -> {
            try {
                s3Service.delete(e.getFilePath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        postRepository.delete(post);
    }

    @Transactional
    public CommentLikeResDto createCommentLike(String email, Long postCommentId) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        PostComment postComment = postCommentRepository.findById(postCommentId).orElseThrow(() -> new CommonNotFoundException("commentNotFound"));

        CommentLike commentLike = commentLikeRepository.findByPostCommentAndUser(postComment, user).orElse(null);
        CommentLikeResDto commentLikeResDto = new CommentLikeResDto();
        if (commentLike != null) {
            commentLikeRepository.delete(commentLike);
            commentLikeResDto = CommentLikeResDto.builder().likeYn("N").build();
        } else {
            CommentLike createdCommentLike = CommentLike.builder().postComment(postComment).user(user).build();
            commentLikeRepository.save(createdCommentLike);
            commentLikeResDto = CommentLikeResDto.builder().likeYn("Y").build();
        }
        return commentLikeResDto;
    }

    @Transactional
    public void uploadCommentAlarm(Post post, User user, PostCommentReqDto postCommentReqDto) {
        // ?????? ????????? (user), ????????? ??????, ??????????????? ?????? ??????
        if (post.getUser().getId() != user.getId()) { // ????????? ???????????? ?????? ???????????? ????????????, ????????? ??????????????? ??????.
            if (postCommentReqDto.getPostCommentParentId() != 0) { // ????????? ????????? ?????? ??????????????? ??????
                PostComment parentComment = postCommentRepository
                        .findById(postCommentReqDto.getPostCommentParentId())
                        .orElseThrow(() -> new CommonNotFoundException("parentCommentNotFound"));

                if (parentComment.getUser().getId() != user.getId()) { // ??????????????? ???????????? ????????????
                    Alarm alarm = Alarm.builder().user(parentComment.getUser()).userSend(user).type("comment").content(
                            user.getNickname() + "?????? ???????????? ????????? ????????? ???????????????." + postCommentReqDto.getContent() + "'"
                    ).readYn("N").build();
                    alarmRepository.save(alarm);
                }
            }

            Alarm alarm = Alarm.builder().user(post.getUser()).userSend(user).type("comment").content(
                    user.getNickname() + "?????? ???????????? ???????????? ????????? ???????????????. '" + postCommentReqDto.getContent() + "'"
            ).readYn("N").build();
            alarmRepository.save(alarm);
        } else {
            if (postCommentReqDto.getPostCommentParentId() != 0) { // ????????? ????????? ?????? ??????????????? ??????
                PostComment parentComment = postCommentRepository
                        .findById(postCommentReqDto.getPostCommentParentId())
                        .orElseThrow(() -> new CommonNotFoundException("parentCommentNotFound"));

                if (parentComment.getUser().getId() != user.getId()) { // ??????????????? ???????????? ????????????
                    Alarm alarm = Alarm.builder().user(parentComment.getUser()).userSend(user).type("comment").content(
                            user.getNickname() + "?????? ???????????? ????????? ????????? ???????????????." + postCommentReqDto.getContent() + "'"
                    ).readYn("N").build();
                    alarmRepository.save(alarm);
                }
            }
        }
        
        //??? ?????? ?????? ?????? ??????
        user.changeNewAlarmYn("Y");
    }

    @Transactional
    public void uploadLikeAlarm(Post post, User user) {
        if (post.getUser() != user) { // ????????? ????????? ????????? ?????? ???????????? ?????? ??????????????? ??????
            Alarm alarm = Alarm.builder()
                    .content(user.getNickname() + "?????? ???????????? ???????????? ???????????? ???????????????.")
                    .user(post.getUser())
                    .userSend(user)
                    .type("like")
                    .readYn("N")
                    .build();
            alarmRepository.save(alarm);
        }

        //??? ?????? ?????? ?????? ??????
        user.changeNewAlarmYn("Y");
    }
}
