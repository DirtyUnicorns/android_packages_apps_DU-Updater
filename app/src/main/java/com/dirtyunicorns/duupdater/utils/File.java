/*
 * Copyright (C) 2015-2018 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dirtyunicorns.duupdater.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class File implements Parcelable, Serializable {
    private String FileName;
    private String FileSize;
    private String FileMD5;
    private String FildLink;

    File(Parcel in) {
        FileName = in.readString();
        FileSize = in.readString();
        FileMD5 = in.readString();
        FildLink = in.readString();
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel in) {
            return new File(in);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };

    void SetFileName(String value) {
        FileName = value;
    }

    void SetFileSize(String value) {
        FileSize = value;
    }

    void SetFileLink(String value) {
        FildLink = value;
    }

    void SetFileMD5(String value) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FileName);
        dest.writeString(FileSize);
        dest.writeString(FileMD5);
        dest.writeString(FildLink);
    }
}