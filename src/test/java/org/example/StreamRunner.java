package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamRunner {
    public static void main(String[] args) {

        System.out.println("Изначальный список целых чисел = " +
                Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13).collect(Collectors.toList()) + "\n");

        System.out.println("1. Удаление дубликатов: " +
                Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                        .distinct()
                        .map(it -> it + " ")
                        .collect(Collectors.toList())
                )
        ;

        System.out.println("2. 3-е наибольшее число = " +
                Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                    .sorted(Comparator.reverseOrder())
                    .limit(3)
                    .min(Integer::compare)
                    .get()
        );

        System.out.println("2. 3-е наибольшее \"уникальное\" число = " +
                Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .skip(2)
                    .limit(1)
                    .findFirst()
                    .get()
        );

        List<Person> persons = new ArrayList<Person>(
                Arrays.asList(
                    new Person("Alexandr", 10, Position.ENGINEER)
                    ,new Person("Bob", 20, Position.MANAGER)
                    ,new Person("Smith", 30, Position.ENGINEER)
                    ,new Person("Michael", 40, Position.DIRECTOR)
                    ,new Person("Arnold", 50, Position.ENGINEER)
                    ,new Person("Max", 60, Position.ENGINEER)
                    ,new Person("John", 70, Position.ENGINEER)
                )
        );

        System.out.println("sum = " +
                persons.stream()
                        .filter(it->it.getPosition()==Position.ENGINEER)
//                        .sorted(Comparator.reverseOrder())
                        .sorted(Comparator.comparingInt(it -> it.getAge()))
                        .map(it -> it.getName())
                        .peek(System.out::println)
                        .sorted(Comparator.reverseOrder())
                        .limit(3)
                        .collect(Collectors.toList()));

        System.out.println("5. Средний возраст ENGINEER = " +
                persons.stream()
                        .filter(it->it.getPosition()==Position.ENGINEER)
                        .collect(Collectors.averagingInt(Person::getAge)
                )
        );
    }
}