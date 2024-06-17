package org.example;

import annotation.AfterSuite;
import annotation.BeforeSuite;
import annotation.CsvSource;
import annotation.Test;

public class Animal {
    @BeforeSuite static void sleep() {
        System.out.println("Animal is sleeping");
    }
    @AfterSuite static void walk() {
        System.out.println("Animal is walking");
    }

    @Test (priority=10) public void eat() {
        System.out.println("Animal is eating");
    }
    @Test public void drink() {
        System.out.println("Animal is drinking");
    }
    @Test (priority = 4) public void run() {
        System.out.println("Animal is running");
    }

    @CsvSource(value = "10, Java, 20, true") public void jump(int p1,String p2,int p3,Boolean p4) {
        System.out.println("p1 = "+p1+", p2 = "+p2+", p3 = "+p3+", p4 = "+p4);
    }
}
