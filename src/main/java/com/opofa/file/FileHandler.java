package com.opofa.file;

import java.io.*;

public class FileHandler {
    public static void read(String path) throws IOException {
        BufferedReader  bufferedReader = new BufferedReader(new FileReader(path));
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        bufferedReader.close();
    }

    public  static void write(String path, String line, boolean append) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, append));
        bufferedWriter.write(line);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
