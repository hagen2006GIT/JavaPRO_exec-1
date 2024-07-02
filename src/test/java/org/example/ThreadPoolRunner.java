package org.example;

import case3.MyThreadPool;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRunner {
    public static void main(String[] args) throws InterruptedException {

        Runnable taskPattern=() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {}
        };

        MyThreadPool w=new MyThreadPool(4);

        Scanner in=new Scanner(System.in);
        int maxTaskCount=0;
        System.out.print("Ввод (число > 0 - генерация задач; 0 - shutdown; -1 выход): ");
        System.out.println();
        while ((maxTaskCount=in.nextInt())!=-1) {
            if (maxTaskCount!=0) {
                for (int i = 0; i < maxTaskCount; i++) {
                    try {
                        w.execute(taskPattern);
                    } catch (IllegalStateException ignored) {}
                }
            } else {
                w.shutdown();
                if (w.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.out.println("Все потоки завершены");
                }
            }
        }
        in.close();
    }
}
