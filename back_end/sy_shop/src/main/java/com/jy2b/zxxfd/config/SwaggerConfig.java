package com.jy2b.zxxfd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        ApiInfo apiInfoBuilder = new ApiInfoBuilder()
                .contact(
                        new Contact("林武泰","localhost:8081/api/swagger-ui.html","2786794141@qq.com")
                )
                .title("sy_shop接口说明文档")
                .description("20级计算机应用班毕业设计——sy_shop接口说明文档")
                .version("1.0")
                .build();
        docket.apiInfo(apiInfoBuilder);
        return docket;
    }
}
