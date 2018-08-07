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

import android.os.AsyncTask;

import com.dirtyunicorns.duupdater.MainActivity;
import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.adapters.CardAdapter;

import java.util.ArrayList;

import static com.dirtyunicorns.duupdater.utils.InterfaceHelper.showSnackBar;

public class GetFiles extends AsyncTask<String, String, ArrayList<File>> {

    private String dir;
    private CardAdapter adapter;
    private Boolean device;

    public GetFiles(String dir, boolean device, CardAdapter adapter) {
        this.dir = dir;
        this.adapter = adapter;
        this.device = device;
    }

    @Override
    protected ArrayList<File> doInBackground(String... params) {
        return Utils.getFiles(dir, device);
    }

    protected void onPostExecute(ArrayList<File> result) {
        if (result.size() > 0) {
            adapter.clear();
            adapter.addItem(result);
        } else {
            adapter.clear();
            showSnackBar(R.string.no_files_to_show,MainActivity.snackbar);
        }
    }
}