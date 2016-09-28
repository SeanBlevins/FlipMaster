package com.arbordenizen.flipmaster;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Sean Blevins on 19/09/2016.
 * Public class used by 3 main activities to perform all file operations
 */
public class FileOperations {

    static public final File flipMasterDir = new File(Environment.getExternalStorageDirectory(),"FlipMaster");
    static public final String PREFS_NAME = "MyPrefsFile";

    //Files to write to SD card from raw folder on first run of app
    static private final String[] includedFiles = { "german_example", "spanish_example", "spanish_nature" };

    //Write the includedFiles.flip files to the SD card
    static public boolean writeResRawFilesToSD(Context context) throws IllegalAccessException {

        if (isExternalStorageWritable() && flipMasterFolderExists()) {

            for (String fileName : includedFiles) {

                String externalPath = flipMasterDir.getAbsolutePath() + "/" + fileName + ".flip";

                //Uses the context from the calling activity to access resources object
                int rid = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());

                try {
                    Resources res = context.getResources();
                    InputStream in = res.openRawResource(rid);
                    FileOutputStream out = new FileOutputStream(externalPath);

                    byte[] buff = new byte[1024];
                    int read = 0;
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }

                    in.close();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;

        }
        return false;
    }

    //Check if FlipMaster directory exists on the SD card, tries to make it if it doesn't
    static public boolean flipMasterFolderExists() {

        if (!flipMasterDir.exists()) flipMasterDir.mkdirs();
        if (flipMasterDir.exists() && flipMasterDir.isDirectory()) {
            return true;
        }
        return false;
    }

    //Checks if external storage is available for read and write
    static public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //Checks if external storage is available to at least read
    static public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
