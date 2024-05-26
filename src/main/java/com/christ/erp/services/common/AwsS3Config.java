package com.christ.erp.services.common;


import java.net.URI;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;

@RequiredArgsConstructor
@Configuration
public class AwsS3Config {

    private final AwsProperties s3ConfigProperties;


    @Bean
    public S3AsyncClient s3AsyncClient(AwsCredentialsProvider awsCredentialsProvider) {

        return S3AsyncClient.builder()
                .httpClient(sdkAsyncHttpClient())
                .region(Region.of(s3ConfigProperties.getRegion()))
                .credentialsProvider(awsCredentialsProvider)
                .endpointOverride(URI.create(s3ConfigProperties.getEndpoint()))
                .forcePathStyle(true)
                .serviceConfiguration(s3Configuration()).build();
    }

    private SdkAsyncHttpClient sdkAsyncHttpClient() {
        return NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();
    }

    private S3Configuration s3Configuration() {
        return S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();
    }
     
    /*@Bean
    AwsCredentialsProvider awsCredentialsProvider() {
        return () -> AwsBasicCredentials.create( System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"));
        														
    }*/
    @Bean
    AwsCredentialsProvider awsCredentialsProvider() {
    	AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
    	return () -> AwsBasicCredentials.create(
    		    credentialsProvider.resolveCredentials().accessKeyId(),
    		    credentialsProvider.resolveCredentials().secretAccessKey()
    		);
        														
    }
    /*
    @Bean
    public SqsAsyncClient amazonSQSAsyncClient(AwsCredentialsProvider awsCredentialsProvider) {
       return SqsAsyncClient.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
     */

    
}
