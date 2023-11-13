package com.eerojala.wordcount.api.context;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class AppConfig {
    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(createCaffeineCacheBuilder());

        return cacheManager;
    }

    private Caffeine<Object, Object> createCaffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumWeight(10000)
                .weigher((k, v) -> {
                    var castedList =  (List) v;

                    return castedList.size(); // Use WordCount list size as weight
                });
    }


}
