package org.example;

import annotation.*;
import java.lang.annotation.Annotation;
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
        Map<Integer,List<Method>> mapMainSuites=new TreeMap<Integer,List<Method>>(Collections.reverseOrder()); //map для выполнения методов в соответствии с их приоритетами
        List<Method> mapBeforeTest=new ArrayList<>(); // список методов, выполняемых ПЕРЕД каждым тестом
        List<Method> mapAfterTest=new ArrayList<>(); // список методов, выполняемых ПОСЛЕ каждого теста
//
        for(Method method:arrMethod){
            Annotation[] annotations=method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof BeforeSuite) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new RuntimeException("Static methods only allowed with BeforeSuite annotation");
                    }
                    if (mapMainSuites.get(Integer.MAX_VALUE)!=null) {
                        throw new RuntimeException("Only one @BeforeSuite are allowed");
                    }
                    mapMainSuites.put(Integer.MAX_VALUE,new ArrayList<Method>(Collections.singleton(method)));
                } else if (annotation instanceof AfterSuite) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new RuntimeException("Static methods only allowed with AfterSuite annotation");
                    }
                    if (mapMainSuites.get(Integer.MIN_VALUE) != null) {
                        throw new RuntimeException("Only one @AfterSuite are allowed");
                    }
                    mapMainSuites.put(Integer.MIN_VALUE, new ArrayList<Method>(Collections.singleton(method)));
                }
// формирование списка на выполнение методов с аннотациями @Test
                 else if (annotation instanceof Test) {
                    if (mapMainSuites.get(method.getAnnotation(Test.class).priority())==null) {
                        mapMainSuites.put(method.getAnnotation(Test.class).priority(),new ArrayList<Method>(Collections.singleton(method)));
                    } else {
                        mapMainSuites.get(method.getAnnotation(Test.class).priority()).add(method);
                    }
                }
// проверка аннотаций @BeforeTest и @AfterTest и подготовка списка выполняемых методов в соответствии с семантикой аннотации
                else if (annotation instanceof BeforeTest) {
                    mapBeforeTest.add(method);
                }
                else if (annotation instanceof AfterTest) {
                    mapAfterTest.add(method);
                }
// проверка на аннотацию @CsvSource
                else if (annotation instanceof CsvSource) {
                    String[] params=method.getAnnotation(CsvSource.class).value().split(",");
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
        }
// выполнение методов класса Animal в порядке убывания приоритетов аннотации @Test
        for (Map.Entry<Integer,List<Method>> entry : mapMainSuites.entrySet()) {
            for (Method method : entry.getValue()) {
    // методы ПЕРЕД каждым тестом
                if (!mapBeforeTest.isEmpty()
                        && !method.isAnnotationPresent(BeforeSuite.class)
                        && !method.isAnnotationPresent(AfterSuite.class)) {
                    for (Method methodBeforeTest : mapBeforeTest) {
                        methodBeforeTest.invoke(animal);
                    }
                }
                method.invoke(animal); // выполнение основного теста
    // методы ПОСЛЕ каждого теста
                if (!mapBeforeTest.isEmpty()
                        && !method.isAnnotationPresent(AfterSuite.class)
                        && !method.isAnnotationPresent(BeforeSuite.class)) {
                    for (Method methodAfterTest : mapAfterTest) {
                        methodAfterTest.invoke(animal);
                    }
                }
            }
        }
    }
}
