package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CacheClient {
    private static Jedis INSTANCE;
    private static Properties CACHE_PROPERTIES;
    private CacheClient() {

    }
    static {
        CACHE_PROPERTIES = new Properties();
        try(InputStream is = CacheClient.class.getClassLoader().getResourceAsStream("cache.properties")) {
            CACHE_PROPERTIES.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Issue while loading cache connection properties");
        }
    }

    public static Jedis getCacheClient() {
        if(INSTANCE == null) {
            INSTANCE =  new Jedis(CACHE_PROPERTIES.getProperty("url"));
        }
        return INSTANCE;
    }

    public static String setKeyWithNoDuplicates(String key, String value) {
        SetParams setParams = new SetParams();
        setParams.ex(10L);
        setParams.nx();
        String set = getCacheClient().set(key, value, setParams);
        System.out.println("SS " + set);
        return set;
    }

}
