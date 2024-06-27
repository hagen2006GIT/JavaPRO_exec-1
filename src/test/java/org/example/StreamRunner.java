package org.example;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamRunner {
    public static void main(String[] args) {

        System.out.println("Изначальный список целых чисел: " +
            Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13).collect(Collectors.toList()) + "\n");

// OK
        System.out.println("1. Удаление дубликатов: " +
            Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                    .distinct()
                    .toList()
            )
        ;

// OK
        System.out.println("2. 3-е наибольшее число = " +
            Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .skip(2)
                .findAny()
                .get()
        );

// OK
        System.out.println("2. 3-е наибольшее \"уникальное\" число = " +
            Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .get()
        );

        List<Person> persons = new ArrayList<>(
            Arrays.asList(
                new Person("Alexandr", 68, Position.ENGINEER)
                ,new Person("Bob", 20, Position.MANAGER)
                ,new Person("Smith", 30, Position.ENGINEER)
                ,new Person("Michael", 40, Position.DIRECTOR)
                ,new Person("Arnold", 25, Position.ENGINEER)
                ,new Person("Max", 27, Position.ENGINEER)
                ,new Person("John", 70, Position.ENGINEER)
            )
        );
        System.out.println("\nСписок сотрудников (Person): "+persons
            .stream()
            .map(Person::toString)
            .toList());

// OK
        System.out.println("4. 3 самых старших сотрудников с должностью «Инженер», в порядке убывания возраста = " +
            persons.stream()
                .filter(it -> it.getPosition()==Position.ENGINEER)
                .sorted(Comparator.comparingInt(Person::getAge).reversed())
                .map(Person::getName)
                .limit(3)
                .collect(Collectors.toList()));

// OK
        System.out.println("5. Средний возраст ENGINEER = " +
            persons.stream()
                    .filter(it->it.getPosition()==Position.ENGINEER)
                    .collect(Collectors.averagingInt(Person::getAge)
            )
        );

// OK
        System.out.println("\n6. Самое длинное слово: " +
            Stream.of("Пять","Пятнадцать","Восемнадцать")
            .max(Comparator.comparingInt(String::length))
            .get()
        );

// OK
        Map<String, Long> mapWords = Arrays.stream("один два один три два пять один два восемь десять один три один десять".split(" "))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("\n7. Количество повторяющихся слов: "+mapWords);

// OK
        System.out.println("\n8. Список в порядке увеличения длины слова: " +
            Arrays.stream("в два а восемнадцать б семь тысяча доля целое".split(" "))
                .sorted(Comparator.comparing(String::length).thenComparing(Function.identity()))
                .toList()
        );

// OK
        String[] arr = {"один два три четыре пять","аб абв абвг абвгд абвгде","домашка по курсу java proPROproPROproPRO","задача номер девять stream api proPROproPROpro111"};
        System.out.println("\nСамое длинное слова из массива строк: "+
            Arrays.stream(arr)
            .flatMap((p) -> Arrays.stream(p.split(" ")))
            .max(Comparator.comparingInt(String::length))
            .get()
        );
    }
}
