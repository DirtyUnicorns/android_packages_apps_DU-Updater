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

package com.dirtyunicorns.duupdater.services;

import android.content.Intent;

import com.dirtyunicorns.duupdater.MainActivity;

public class TileService extends android.service.quicksettings.TileService {

    @Override
    public void onClick() {
        super.onClick();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        startActivityAndCollapse(intent);
    }
}