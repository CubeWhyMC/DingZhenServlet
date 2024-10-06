package fuck.manthe.nmsl.config;

import fuck.manthe.nmsl.util.EnhancedDns;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpConfig {
    @Resource
    EnhancedDns enhancedDns;

    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .dns(enhancedDns)
                .build();
    }
}
