package org.example;

import case3.MyThreadPool;

import java.util.concurrent.*;

public class ThreadPoolRunner {
    public static void main(String[] args) throws InterruptedException {

        final int maxTaskPlanned = 6;
        MyThreadPool workThreadPool = new MyThreadPool(4);
        final CountDownLatch latch = new CountDownLatch(maxTaskPlanned);

        Runnable taskPattern = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Ошибка в потоке: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        };
        for (int i = 0; i < maxTaskPlanned; i++) {
            try {
                workThreadPool.execute(taskPattern);
            } catch (IllegalStateException ignored) {
            }
        }
        try {
            latch.await();
            System.out.println("latch.getCount = " + latch.getCount());
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted.", e);
        }
        System.out.println("latch passed");
        workThreadPool.getStatesOfThreadPool();
        workThreadPool.shutdown();
        if (workThreadPool.awaitTermination(3, TimeUnit.SECONDS)) {
            System.out.println("Все потоки завершены");
        }
        workThreadPool.getStatesOfThreadPool();
    }
}
