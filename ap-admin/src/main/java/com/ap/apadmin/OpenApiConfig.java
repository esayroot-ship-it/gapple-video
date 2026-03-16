package com.ap.apadmin;


import com.ap.apadmin.controller.user;
import com.ap.apcommon.entity.*;
import com.ap.apcommon.entity.user.*;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.entity.video.rating;
import com.ap.apcommon.entity.video.series;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .openapi("3.1.0")
                .info(new Info()
                        .title("Spring Boot + Vue 项目接口文档")
                        .version("v1.0.0")
                        .description("这是项目的后端 API 接口服务")
                        .contact(new Contact().name("开发者").email("dev@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .packagesToScan("com.ap.apadmin.controller")
                .addOpenApiCustomizer(openApi -> {
                    Map<String, Schema> schemas = ModelConverters.getInstance().read(feedback.class);
                    openApi.getComponents().getSchemas().putAll(schemas);
                })
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .packagesToScan("com.ap.apservice.controller")
                .addOpenApiCustomizer(openApi -> {
                    Class<?>[] entities = {
                            Video.class,
                            category.class,
                            banner.class,
                            announcement.class,
                            com.ap.apcommon.entity.user.User.class,
                            feedback.class,
                            comment.class,
                            commentlike.class,
                            favorite.class,
                            subsrcibe.class,
                            history.class,
                            episode.class,
                            series.class,
                            rating.class,
                            hot.class
                    };

                    for (Class<?> entity : entities) {
                        Map<String, Schema> schemas = ModelConverters.getInstance().read(entity);
                        openApi.getComponents().getSchemas().putAll(schemas);
                    }
                })
                .build();
    }

}
