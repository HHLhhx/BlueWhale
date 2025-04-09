package com.seecoder.BlueWhale.util;

public class RedisConstants {
    public static final String USER_PHONE_BLOOM_FILTER = "userPhoneBloomFilter";
    public static final long BLOOM_FILTER_SIZE = 1000000L;
    public static final double BLOOM_FILTER_ERR_RATE = 0.01;

    public static final String API_CALL_COUNT_KEY = "api_call_count:";
    public static final int API_CALL_COUNT_EXPIRE_TIME = 30 * 24 * 60 * 60;

    public static final String LOGIN_TOKEN_KEY = "login:token:";
    public static final int LOGIN_TOKEN_EXPIRE_TIME = 6 * 60 * 60;
}
