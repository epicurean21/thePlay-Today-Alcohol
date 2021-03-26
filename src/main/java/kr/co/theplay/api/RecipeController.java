package kr.co.theplay.api;

import io.swagger.annotations.Api;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"104. Recipe (레시피)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "RecipeLogger")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final ResponseService responseService;
    private final PostService postService;
}
