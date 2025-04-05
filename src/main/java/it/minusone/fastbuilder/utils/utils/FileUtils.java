package it.minusone.fastbuilder.utils.utils;

import java.io.File;

public class FileUtils {
    public static String getExtension(String f) {
        int index = f.lastIndexOf(46);
        index = Math.max(f.lastIndexOf(47), f.lastIndexOf(92)) > index ? -1 : index;
        return index == -1 ? "" : f.substring(index + 1);
    }

    public static void deleteDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                try {
                    if (file.isDirectory())
                        deleteDirectory(file);
                    else
                        file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dir.delete();
        }
    }

}