package com.company.engine.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Class.forName(FileUtils.class.getName()).getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> lines = new ArrayList<>();

        try(BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(Class.forName(FileUtils.class.getName()).getResourceAsStream(fileName))
        )) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    public static boolean resourceFileExists(String fileName) {
        boolean result = false;

        try (InputStream inputStream = FileUtils.class.getResourceAsStream(fileName)) {
            result = inputStream != null;
        } catch (Exception e) {
            result = false;
        }

        return result;
    }
}
