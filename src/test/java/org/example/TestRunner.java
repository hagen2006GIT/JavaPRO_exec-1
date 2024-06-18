package org.example;

import annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        runTests(Animal.class);
    }
    static void runTests(Class c) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Method[] arrMethod= c.getDeclaredMethods();
        Constructor<Animal> constructor=c.getConstructor();
        Animal animal=constructor.newInstance();
        Method methodBeforeSuite=null;
        Method methodAfterSuite=null;
        List<Method> mapBeforeTest=new ArrayList<>(); // список методов, выполняемых ПЕРЕД каждым тестом
        List<Method> mapAfterTest=new ArrayList<>(); // список методов, выполняемых ПОСЛЕ каждого теста
        ArrayList<Method> mapTests=new ArrayList<>(); // список основных "тестов"

        for(Method method:arrMethod) {
            if (method.getAnnotation(BeforeSuite.class) != null) { // проверим метод с аннотацией @BeforeSuite
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("Static methods only allowed with BeforeSuite annotation");
                }
                if (methodBeforeSuite != null) {
                    throw new RuntimeException("Only one @BeforeSuite are allowed");
                }
                methodBeforeSuite = method;
            } else if (method.getAnnotation(AfterSuite.class) != null) { // проверим метод с аннотацией @AfterSuite
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("Static methods only allowed with AfterSuite annotation");
                }
                if (methodAfterSuite != null) {
                    throw new RuntimeException("Only one @AfterSuite are allowed");
                }
                methodAfterSuite = method;
            } else if (method.getAnnotation(BeforeTest.class) != null) { // проверим метод с аннотацией @BeforeTest
                mapBeforeTest.add(method);
            } else if (method.getAnnotation(AfterTest.class) != null) { // проверим метод с аннотацией @AfterTest
                mapAfterTest.add(method);
            } else if (method.getAnnotation(Test.class) != null) { // соберем список методов с аннотацией @Test
                mapTests.add(method);
            } else if (method.getAnnotation(CsvSource.class) != null) { // обработка методов с аннотацией @CsvSource
                String[] params = method.getAnnotation(CsvSource.class).value().split(",");
                Class[] paramTypes = method.getParameterTypes();
                Object[] paramsArray = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    switch (paramTypes[i].getName()) {
                        case "java.lang.String":
                            paramsArray[i] = params[i].trim();
                            break;
                        case "int":
                            paramsArray[i] = Integer.parseInt(params[i].trim());
                            break;
                        case "java.lang.Boolean":
                            paramsArray[i] = Boolean.parseBoolean(params[i]);
                            break;
                    }
                }
                System.out.println("*");
                method.invoke(animal,paramsArray);
                System.out.println("*");
            }
        }
        mapTests.sort(new PriorityComparator()); // сортировка списка "тестов" в порядке убывания приоритета
        if (methodBeforeSuite!=null) { // выполнение метода @BeforeSuite (если есть)
            methodBeforeSuite.invoke(animal);
        }
        for (Method m:mapTests) { // выполнение методов класса Animal в порядке убывания приоритетов аннотации @Test
            if (!mapBeforeTest.isEmpty()) {
                for (Method methodBeforeTest : mapBeforeTest) {
                    methodBeforeTest.invoke(animal);
                }
            }
            m.invoke(animal);
            if (!mapAfterTest.isEmpty()) { // выполнение метода @AfterSuite (если есть)
                for (Method methodAfterTest : mapAfterTest) {
                    methodAfterTest.invoke(animal);
                }
            }
        }
        if (methodAfterSuite!=null) { // выполнение метода @AfterSuite (если есть)
            methodAfterSuite.invoke(animal);
        }
    }
}
