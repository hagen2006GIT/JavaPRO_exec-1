package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Person {
    private final String name;
    private final int age;
    private final Position position;
}
