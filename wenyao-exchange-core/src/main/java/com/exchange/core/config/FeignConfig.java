package com.exchange.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.exchange.core.provider.feign"})
public class FeignConfig {

    @Bean
    public Client client() {
        final HttpClientBuilder builder = HttpClientBuilder.create()
                .setMaxConnPerRoute(300)
                .setMaxConnTotal(300);
        return new ApacheHttpClient(builder.build());
    }

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .client(client());
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Decoder decoder() {
        return new GsonDecoder(createGson());//返回空数组
    }

    @Bean
    public Encoder encoder() {
        return new GsonEncoder(createGson());//返回空数组
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();
    }

}
