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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.adapters.CardAdapter;
import com.dirtyunicorns.duupdater.utils.GetFiles;
import com.dirtyunicorns.duupdater.utils.Utils;

import static com.dirtyunicorns.duupdater.utils.Utils.readProp;

public class Gapps extends Official {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, containter, false);

        RecyclerView rv = rootView.findViewById(R.id.rv);
        Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        rv.setAnimation(anim);
        rv.animate();
        CardAdapter adapter = new CardAdapter(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        String cpu;
        if (readProp("ro.product.cpu.abi", false).contains("arm64")) {
            cpu = "gapps/arm64";
        } else {
            cpu = "gapps/arm";
        }

        GetFiles getGapps = new GetFiles(cpu, false, adapter);

        if (Utils.isOnline(getActivity())) {
            getGapps.execute();
        }

        return rootView;
    }
}