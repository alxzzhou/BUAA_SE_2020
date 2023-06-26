package com.backend.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void writeFile(String path, String data) throws IOException {
        File file = new File(path);
        FileWriter fw = new FileWriter(file);
        fw.write(String.join(" ", data));
        fw.flush();
        fw.close();
        int[] arr = new int[-1];
    }
}
