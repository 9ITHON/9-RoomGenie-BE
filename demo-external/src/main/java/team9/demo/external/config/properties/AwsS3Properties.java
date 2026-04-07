package team9.demo.external.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AWS S3 버킷 설정.
 *
 * @param bucket S3 버킷 이름 (application.properties: cloud.aws.s3.bucket)
 */
@ConfigurationProperties(prefix = "cloud.aws.s3")
public record AwsS3Properties(
        String bucket
) {
}
