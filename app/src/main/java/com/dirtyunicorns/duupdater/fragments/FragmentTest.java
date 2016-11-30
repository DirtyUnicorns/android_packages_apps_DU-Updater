package com.dirtyunicorns.duupdater.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.adapters.CardAdapter;
import com.dirtyunicorns.duupdater.utils.File;
import com.dirtyunicorns.duupdater.utils.NetUtils;
import com.dirtyunicorns.duupdater.utils.ServerUtils;

import java.util.ArrayList;

/**
 * Created by mazwoz on 7/5/16.
 */
public class FragmentTest extends Fragment {

    private ArrayList<File> files;
    private ServerUtils su;
    private RecyclerView rv;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_view,containter,false);

        rv = (RecyclerView) rootView.findViewById(R.id.rv);
        Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        rv.setAnimation(anim);
        rv.animate();
        if (NetUtils.isOnline(getActivity())) {
            su = new ServerUtils();
            files = new ArrayList<>();
            GetFiles getFiles = new GetFiles();
            getFiles.execute();

        }

        return rootView;
    }

    private class GetFiles extends AsyncTask<String, String, ArrayList<File>> {

        @Override
        protected ArrayList<File> doInBackground(String... params) {
            files = su.getFiles("Test",true);

            return files;
        }

        protected void onPostExecute(ArrayList<File> result) {
            if (result != null) {
                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                rv.setLayoutManager(llm);
                if (files != null && files.size() > 0) {
                    CardAdapter adapter = new CardAdapter(files, getActivity());
                    rv.setAdapter(adapter);
                } else {
                    Snackbar.make(rootView, getString(R.string.no_files_to_show), Snackbar.LENGTH_INDEFINITE);
                }
            }
        }
    }
}
