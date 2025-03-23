package com.seecoder.BlueWhale.util;


import java.util.ArrayList;
import java.util.List;

public class PermutationUtil {
    private static <T> void addPermutation(List<T> remain, List<T> current, List<List<T>> result) {
        if (remain.isEmpty())
            result.add(current);

        for (int i = 0; i < remain.size(); i++) {
            T t = remain.remove(i);
            current.add(t);
            addPermutation(remain, current, result);
            current.remove(current.size() - 1);
            remain.add(i, t);
        }
    }

    public static <T> List<List<T>> permutationOf(List<T> arr) {
        List<List<T>> ret = new ArrayList<>();
        addPermutation(arr, new ArrayList<>(), ret);
        return ret;
    }
}
