package com.erdioran.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;

public class Helper {

    private static final Logger LOGGER = LogManager.getLogger(Helper.class);

    private Helper() {
    }


    public static void deleteAllFiles(String fileExtension, String path, String fileNameToSearch) {
        String[] fileNames = new File(path).list();
        for (String fileName : fileNames) {
            if (fileName.contains(fileExtension) && fileName.contains(fileNameToSearch)) {
                boolean deleteQuietly = FileUtils.deleteQuietly(Paths.get(path, fileName).toFile());
                if(deleteQuietly){
                    LOGGER.info("Successfully deleted {}", fileName);
                }
            }
        }
    }

    public static void sleepInSeconds(int sleepInSeconds) {
        try {
            LOGGER.debug("wait for seconds : " + sleepInSeconds);
            Thread.sleep(sleepInSeconds * 1000L);
        } catch (Exception e) {
            //
        }
    }

    public static void sleepMs(int sleepInMiliSeconds) {
        try {
            LOGGER.debug("wait for seconds : " + sleepInMiliSeconds);
            Thread.sleep(sleepInMiliSeconds);
        } catch (Exception e) { }
    }
}
