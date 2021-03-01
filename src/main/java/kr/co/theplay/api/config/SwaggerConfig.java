package kr.co.theplay.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    //new로 생성하는 듯 하지만 싱글톤 패턴이라서 최초 한번만 메모리를 할당하고 static 메모리에 인스턴스를 만들어서 사용한다.
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(swaggerInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("kr.co.theplay.api")) //해당 패키지 중 RequestMapping으로 할당된 URL 리스트 추출
                .apis(RequestHandlerSelectors.any()) //RequestMapping 된 모든 URL 리스트 추출
                .paths(PathSelectors.ant("/v1/**")) //그 중 /api/**인 URL들만 필터링
                .build();
    }

    private ApiInfo swaggerInfo(){
        return new ApiInfoBuilder().title("오늘 한 주 API 문서")
                .description("오늘 한 주 앱 개발에 사용되는 서버 API에 대한 연동 문서입니다.")
                .license("theplay").build();
    }
}
