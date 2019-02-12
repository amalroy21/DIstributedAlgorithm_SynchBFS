/**
 *
 * GetAllFiles.java - Get all the files hosted on local directory for the TCPServer
 *
 */

package com.utd.distributed.util;

import java.io.File;
import java.util.concurrent.Callable;

public class GetAllFiles implements Callable{

    private static String directoryPath;

    public GetAllFiles(String directoryPath)  {
        this.directoryPath = directoryPath;
    }

    @Override
    public String call() {
        try {
            File folder = new File(directoryPath);
            File[] listOfFiles = folder.listFiles();

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    sb.append(listOfFiles[i].getName());
                    sb.append(", ");
                }
            }
            return sb.toString().substring(0, sb.length()-2);
        }   catch (Exception ex)    {
            ex.printStackTrace();
            return "error occurred unable to read directory";
        }
    }
}
