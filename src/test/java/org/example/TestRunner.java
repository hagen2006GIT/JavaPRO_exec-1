package org.example;

import annotation.AfterSuite;
import annotation.BeforeSuite;
import annotation.CsvSource;
import annotation.Test;

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
        Class<Animal> animalClass=Animal.class;
        Method[] arrMethod=animalClass.getDeclaredMethods();
        Constructor<Animal> constructor=animalClass.getConstructor();
        Animal animal= constructor.newInstance();
        Map<String,Method> map=new TreeMap<String,Method>(); //map для упорядоченного списка выполнения методов

// посчитать количество методов с аннотациями @BeforeSuite и @AfterSuite
        for(Method method:arrMethod){
            Annotation[] annotations=method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof BeforeSuite) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new RuntimeException("Static methods only allowed with BeforeSuite annotation");
                    }
                    if (map.get("00")!=null) {
                        throw new RuntimeException("Only one @BeforeSuite are allowed");
                    }
                    map.put("00",method);
                } else if (annotation instanceof AfterSuite) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new RuntimeException("Static methods only allowed with AfterSuite annotation");
                    }
                    if (map.get("99")!=null) {
                        throw new RuntimeException("Only one @AfterSuite are allowed");
                    }
                    map.put("99",method);
                } else if (annotation instanceof Test) {
                    map.put(method.getAnnotation(Test.class).priority()+"-"+method.getName(),method);
                }
                else if (annotation instanceof CsvSource) {
                    String[] params=method.getAnnotation(CsvSource.class).value().split(",");
                    Class[] paramTypes = method.getParameterTypes();
                    Object[] paramsArray = new Object[paramTypes.length];
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (paramTypes[i].getName()=="java.lang.String") {
                            paramsArray[i]=params[i];
                        } else if (paramTypes[i].getName()=="int") {
                            paramsArray[i]=Integer.parseInt(params[i].trim());
                        } else if (paramTypes[i].getName()=="java.lang.Boolean") {
                            paramsArray[i]=Boolean.parseBoolean(params[i]);
                        }
                    }
                    method.invoke(animal,paramsArray);
                }
            }
        }
// выполнение методов класса Animal
//        Collections.sort(map.keySet(), new Comparator);
//        for (Map.Entry<String,Method> entry : map.entrySet()) {
//            System.out.println(map.get(entry.getKey()));
//            entry.getValue().invoke(animal);
//        }
/*        assert beforeSuiteMethod != null;
        beforeSuiteMethod.invoke(animal);
        assert afterSuiteMethod != null;
        afterSuiteMethod.invoke(animal);*/
    }
}
