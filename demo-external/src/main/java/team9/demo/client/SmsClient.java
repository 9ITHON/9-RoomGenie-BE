package team9.demo.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import team9.demo.dto.SmsMessageDto;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class SmsClient {

    @Value("${ncp.sms.accessKey}")
    private String accessKey;

    @Value("${ncp.sms.secretKey}")
    private String secretKey;

    @Value("${ncp.sms.serviceId}")
    private String serviceId;

    private final String baseUrl = "https://sens.apigw.ntruss.com";

    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Map<String, Object> send(SmsMessageDto dto) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = "/sms/v2/services/" + serviceId + "/messages";

            return webClient.post()
                    .uri(path)
                    .header("x-ncp-apigw-timestamp", timestamp)
                    .header("x-ncp-iam-access-key", accessKey)
                    .header("x-ncp-apigw-signature-v2", makeSignature(timestamp, path, HttpMethod.POST))
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("SMS 전송 실패", e);
            throw new RuntimeException("SMS 전송 실패", e);
        }
    }

    private String makeSignature(String timestamp, String urlPath, HttpMethod method)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        String space = " ";
        String newLine = "\n";
        String message = method.name() + space + urlPath + newLine + timestamp + newLine + accessKey;

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(rawHmac);
    }
}