package com.scriptopia.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${image-dir}")
    private String imageDir;

    @Value("${image-url-prefix:/images}")
    private String imageUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imageUrlPrefix + "/**")
                .addResourceLocations("file:" + imageDir);
    }
}
