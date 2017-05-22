package com.dirtyunicorns.duupdater.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dirtyunicorns.duupdater.MainActivity;
import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.adapters.CardAdapter;
import com.dirtyunicorns.duupdater.utils.GetFiles;
import com.dirtyunicorns.duupdater.utils.Utils;

/**
 * Created by mazwoz on 7/5/16.
 */
public class FragmentOfficial extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, containter, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
        Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        rv.setAnimation(anim);
        rv.animate();
        CardAdapter adapter = new CardAdapter(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        if (Utils.isOnline(getActivity())) {
            GetFiles getFiles = new GetFiles("Official", true, adapter, (MainActivity) getActivity());
            getFiles.execute();
        }

        return rootView;
    }
}