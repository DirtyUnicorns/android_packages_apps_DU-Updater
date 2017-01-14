/*
* Copyright (C) 2015 Dirty Unicorns
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mazwoz on 12/18/14.
 */
public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;

    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url) {

        HttpsURLConnection client = null;
        try{
            URL jsonURL = new URL(url);
            client = (HttpsURLConnection) jsonURL.openConnection();
            client.setRequestMethod("GET");
            client.setRequestProperty("Content-Type","application/json");
            is = new BufferedInputStream(client.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            jObj = new JSONObject(sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jObj;

    }
}
