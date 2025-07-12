package team9.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "team9.demo.jparepository",
        "team9.demo.jpaentity"
})
public class JpaConfig {
    // 추가 설정이 있다면 여기에 작성
}