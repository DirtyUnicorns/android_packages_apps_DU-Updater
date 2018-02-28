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

package com.dirtyunicorns.duupdater.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class Update {

    int majorVersion;
    int minorVersion;
    String androidVersion;
    String buildType;
    Date buildDate;
    boolean isOffical;
    boolean isWeekly;
    boolean isRc;

    String GetProp() {
        String retProp = null;

        try {
            Process process = new ProcessBuilder("/system/bin/getprop", "ro.du.version").redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                retProp = line;
            }
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retProp;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public boolean isOfficial() {
        return isOffical;
    }

    public boolean isWeekly() {
        return isWeekly;
    }

    public boolean isRc() {
        return isRc;
    }
}