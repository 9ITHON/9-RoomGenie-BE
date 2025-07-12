package team9.demo.client;



import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import team9.demo.implementation.contact.PhoneNumber;


@Slf4j
@Component
public class AuthCacheClient {

    private final Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .recordStats()
            .build();

    public String cacheVerificationCode(PhoneNumber phoneNumber, String verificationCode) {
        log.info("Cache verification code: {}", verificationCode);
        cache.put(phoneNumber.getE164PhoneNumber(), verificationCode);
        return verificationCode;
    }

    public void removeVerificationCode(PhoneNumber phoneNumber) {
        cache.invalidate(phoneNumber.getE164PhoneNumber());
    }

    public String getVerificationCode(PhoneNumber phoneNumber) {
        return cache.getIfPresent(phoneNumber.getE164PhoneNumber());
    }
}