package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {


    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(4);


       // System.out.println(jedis.get("Message"));
        Future<String> submit = executorService.submit(() -> {
            String setnx = CacheClient.setKeyWithNoDuplicates("TT4", "HI");
            latch.countDown();
            return setnx;
        });

        Future<String> submit1 = executorService.submit(() -> {
            String setnx = CacheClient.setKeyWithNoDuplicates("TT4", "HI");
            latch.countDown();
            return setnx;
        });
        Future<String> submit2 = executorService.submit(() -> {
            String setnx = CacheClient.setKeyWithNoDuplicates("TT4", "HI");
            latch.countDown();
            return setnx;
        });
        Future<String> submit3 = executorService.submit(() -> {
            String setnx = CacheClient.setKeyWithNoDuplicates("TT4", "HI");
            latch.countDown();
            return setnx;
        });
        latch.await();

        System.out.println(submit.get());
        System.out.println(submit1.get());
        System.out.println(submit2.get());
        System.out.println(submit3.get());
        executorService.shutdownNow();
    }
}