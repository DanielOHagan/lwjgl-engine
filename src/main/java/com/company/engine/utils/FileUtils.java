package com.company.engine.utils;

import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static ByteBuffer ioResourceToByteBuffer(
            String fileName,
            int bufferSize
    ) throws IOException {
        ByteBuffer byteBuffer;
        Path path = Paths.get(fileName);

        if (Files.isReadable(path)) {
            try (SeekableByteChannel sbc = Files.newByteChannel(path)) {
                byteBuffer = BufferUtils.createByteBuffer((int) sbc.size() + 1);
                while (sbc.read(byteBuffer) != -1);
            }
        } else {
            try (
                    InputStream source = FileUtils.class.getResourceAsStream(fileName);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                byteBuffer = BufferUtils.createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(byteBuffer);

                    if (bytes == -1) {
                        break;
                    }

                    if (byteBuffer.remaining() == 0) {
                        byteBuffer = resizeBuffer(byteBuffer, byteBuffer.capacity() * 2);
                    }
                }
            }
        }

        if (byteBuffer != null) {
            byteBuffer.flip();
        }

        return byteBuffer;
    }

    private static ByteBuffer resizeBuffer(
            ByteBuffer byteBuffer,
            int capacity
    ) {
        ByteBuffer newByteBuffer = BufferUtils.createByteBuffer(capacity);

        byteBuffer.flip();
        newByteBuffer.put(byteBuffer);

        return newByteBuffer;
    }
}