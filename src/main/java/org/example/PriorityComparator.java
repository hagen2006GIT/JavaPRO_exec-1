package org.example;

import annotation.Test;
import java.lang.reflect.Method;
import java.util.Comparator;

class PriorityComparator implements Comparator {
    public int compare(Object obj1, Object obj2) {
        Method m1=(Method) obj1;
        Method m2=(Method) obj2;
        Integer s1=m1.getAnnotation(Test.class).priority();
        Integer s2=m2.getAnnotation(Test.class).priority();
        return s2.compareTo(s1);
    }
}