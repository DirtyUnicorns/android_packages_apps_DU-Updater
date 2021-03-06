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

package com.dirtyunicorns.duupdater.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.adapters.CardAdapter;
import com.dirtyunicorns.duupdater.utils.SwipeRefresh;
import com.dirtyunicorns.duupdater.utils.GetFiles;
import com.dirtyunicorns.duupdater.utils.Utils;

public class Gapps extends Base {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_view, containter, false);

        RecyclerView rv = rootView.findViewById(R.id.rv);
        adapter = new CardAdapter(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchGapps();
                        pullToRefresh.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        fetchGapps();

        return rootView;
    }

    private void fetchGapps() {
        if (Utils.isOnline(getActivity())) {
            GetFiles getGapps = new GetFiles(Utils.isCPU(), false, adapter);
            getGapps.execute();
        }
    }
}