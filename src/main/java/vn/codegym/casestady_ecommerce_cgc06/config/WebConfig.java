package vn.codegym.casestady_ecommerce_cgc06.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    void ensureUploadDir() throws Exception {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        log.info("Static image dir: {}", path.toUri()); // ví dụ: file:/D:/ecommerce-uploads/
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /img/** tới thư mục trên ổ đĩa
        String location = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        // location sẽ có dạng file:/D:/ecommerce-uploads/
        registry.addResourceHandler("/img/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }

}
