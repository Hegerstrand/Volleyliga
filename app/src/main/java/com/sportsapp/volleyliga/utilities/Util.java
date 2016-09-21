package com.volleyapp.volleyliga.utilities;

import java.util.ArrayList;
import java.util.Collection;

public class Util {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.equalsIgnoreCase("");
    }

    public static int getRoundedValue(double value) {
        return (int) Math.round(value);
    }

    public static <E> Collection<E> makeCollection(Iterable<E> iter) {
        Collection<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }
}
