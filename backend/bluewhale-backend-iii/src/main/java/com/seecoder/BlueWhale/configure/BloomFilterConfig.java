package com.seecoder.BlueWhale.configure;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.seecoder.BlueWhale.util.RedisConstants.USER_PHONE_BLOOM_FILTER;

@Configuration
public class BloomFilterConfig {


    @Bean
    public RBloomFilter<String> registerBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(USER_PHONE_BLOOM_FILTER);
        bloomFilter.tryInit(1000000L, 0.01);
        return bloomFilter;
    }

}
