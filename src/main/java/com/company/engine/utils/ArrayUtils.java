package com.company.engine.utils;

import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;

public class ArrayUtils {

    public static int[] listToIntArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }

    public static float[] listToFloatArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArray = new float[size];

        for (int i = 0; i < size; i++) {
            floatArray[i] = list.get(i);
        }

        return floatArray;
    }

    public static int[] createIntArray(int length) {
        int[] intArray = new int[length];

        Arrays.fill(intArray, 0);

        return intArray;
    }

    public static int[] createIntArray(int length, int defaultValue) {
        int[] intArray = new int[length];

        Arrays.fill(intArray, defaultValue);

        return intArray;
    }

    public static float[] createFloatArray(int length, float defaultValue) {
        float[] floatArray = new float[length];

        Arrays.fill(floatArray, defaultValue);

        return floatArray;
    }

    public static float[] createFloatArray(int length) {
        float[] floatArray = new float[length];

        Arrays.fill(floatArray, 0);

        return floatArray;
    }

    public static Vector4f[] createVector4fArray(int length, Vector4f defaultValue) {
        Vector4f[] array = new Vector4f[length];

        Arrays.fill(array, defaultValue);

        return array;
    }

    public static Vector4f[] createVector4fArray(int length) {
        Vector4f[] array = new Vector4f[length];

        Arrays.fill(array, new Vector4f(1, 1, 1, 1));

        return array;
    }
}