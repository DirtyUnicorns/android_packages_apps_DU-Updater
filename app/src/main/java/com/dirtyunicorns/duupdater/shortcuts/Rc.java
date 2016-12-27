package com.dirtyunicorns.duupdater.shortcuts;

import android.os.Bundle;

import com.dirtyunicorns.duupdater.MainActivity;
import com.dirtyunicorns.duupdater.R;

public class Rc extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitInterface();
        InitPermissions();
        InitRc();
    }
}