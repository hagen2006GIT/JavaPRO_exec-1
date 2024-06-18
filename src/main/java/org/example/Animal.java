package org.example;

import annotation.*;

public class Animal {
    @BeforeSuite static void sleep() {
        System.out.println("Animal is sleeping");
    }
    @AfterSuite static void walk() {
        System.out.println("Animal is walking");
    }
// *************************** основные тесты
    @Test (priority=10) public void eat() {
        System.out.println("Animal is eating");
    }
    @Test public void drink() {
        System.out.println("Animal is drinking");
    }
    @Test (priority = 6) public void run() {
        System.out.println("Animal is running");
    }
    @Test (priority = 7) public void swim() {
        System.out.println("Animal is swimming");
    }
// ***************************
    @BeforeTest public void fly() {
        System.out.println("Animal is flying - @BeforeTest");
    }
    @BeforeTest public void land() {
        System.out.println("Animal is landing - @BeforeTest");
    }
    @AfterTest public void takeOff() {
        System.out.println("Animal is taking off - @AfterTest");
    }
// ***************************
    @CsvSource(value = "10, Java, 20, true") public void jump(int p1,String p2,int p3,Boolean p4) {
        System.out.println("CsvSource params:\n"+"\tp1 = "+p1+"\n\tp2 = "+p2+"\n\tp3 = "+p3+"\n\tp4 = "+p4);
    }
}
