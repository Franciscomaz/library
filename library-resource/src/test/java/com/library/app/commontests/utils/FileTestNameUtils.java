package com.library.app.commontests.utils;

import org.junit.Ignore;

@Ignore
public class FileTestNameUtils {
    private static  String REQUEST_PATH = "/request/";
    private static  String RESPONSE_PATH = "/response/";

    private FileTestNameUtils() {
    }

    public static String getFileRequestPath(final String mainFolder, final String fileName) {
        return mainFolder + REQUEST_PATH + fileName;
    }

    public static String getFileResponsePath(final String mainFolder, final String fileName) {
        return mainFolder + RESPONSE_PATH + fileName;
    }
}
