package com.example.lifetutor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) { //기본 컨버터 유지관리
        converters.removeIf(v -> v.getSupportedMediaTypes().contains(MediaType.APPLICATION_JSON)); //기존 json용 컨버터 제거
        converters.add(new MappingJackson2HttpMessageConverter()); //새로 json 컨버터 추가
    }
}
