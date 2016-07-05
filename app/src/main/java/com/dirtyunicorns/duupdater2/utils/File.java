package com.dirtyunicorns.duupdater2.utils;

/**
 * Created by mazwoz on 7/5/16.
 */
public class File {
    private static String FileName;
    private static String FileSize;
    private static String FileMD5;
    private static String FildLink;

    public void SetFileName(String value) {
        FileName = value;
    }

    public void SetFileSize(String value) {
        FileSize = value;
    }

    public void SetFileLink(String value) {
        FildLink = value;
    }

    public void SetFileMD5(String value) {
        FileMD5 = value;
    }

    public String GetFileName() {
        return FileName;
    }

    public String GetFileSize() {
        return FileSize;
    }

    public String GetFileMD5() {
        return FileMD5;
    }

    public String GetFileLink() {
        return FildLink;
    }
}
