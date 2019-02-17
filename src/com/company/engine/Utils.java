package com.company.engine;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static int[] listToIntArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }

    public static boolean resourceFileExists(String fileName) {
        boolean result = false;

        try (InputStream inputStream = Utils.class.getResourceAsStream(fileName)) {
            result = inputStream != null;
        } catch (Exception e) {
            result = false;
        }

        return result;
    }
}
