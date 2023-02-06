package Forcloud.chat.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        // TODO: allowedOrigins-> 우리 서버 ip로 수정 필요 ex)http://192.126.32.2:3000
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
