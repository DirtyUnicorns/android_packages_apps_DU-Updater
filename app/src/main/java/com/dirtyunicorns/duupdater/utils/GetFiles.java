package com.dirtyunicorns.duupdater.utils;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.dirtyunicorns.duupdater.MainActivity;
import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.adapters.CardAdapter;

import java.util.ArrayList;

/**
 * Created by ngengs on 12/31/2016.
 */

public class GetFiles extends AsyncTask<String, String, ArrayList<File>> {

    private String dir;
    private CardAdapter adapter;
    private MainActivity activity;
    private Boolean device;

    public GetFiles(String dir, boolean device, CardAdapter adapter, MainActivity activity) {
        this.dir = dir;
        this.adapter = adapter;
        this.activity = activity;
        this.device = device;
    }

    @Override
    protected ArrayList<File> doInBackground(String... params) {
        return ServerUtils.getFiles(dir, device);
    }

    protected void onPostExecute(ArrayList<File> result) {
        if (!result.isEmpty()) {
            adapter.addItem(result);
        } else {
            activity.showSnackBar(R.string.no_files_to_show);
        }
    }
}