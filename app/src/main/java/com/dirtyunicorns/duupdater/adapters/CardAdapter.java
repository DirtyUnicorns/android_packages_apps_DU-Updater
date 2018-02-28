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

package com.dirtyunicorns.duupdater.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.services.DownloadService;
import com.dirtyunicorns.duupdater.utils.File;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.FileHolder>{

    private ArrayList<File> files;
    private Context ctx;
    private Intent intent;

    public CardAdapter(Context ctx){
        this.ctx = ctx;
        this.files = new ArrayList<>();
    }

    public CardAdapter(ArrayList<File> files, Context ctx) {
        this.files = files;
        this.ctx = ctx;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cardview, parent, false);
        return new FileHolder(v);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        holder.fileName.setText(files.get(position).GetFileName());
        holder.fileSize.setText(String.format(ctx.getString(R.string.card_file_size), files.get(pos).GetFileSize()));
        holder.fileMD5.setText(String.format(ctx.getString(R.string.card_file_md5), files.get(pos).GetFileMD5()));
        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = files.get(pos).GetFileLink();
                intent = new Intent(ctx, DownloadService.class);
                intent.putExtra("url", link);
                intent.putExtra("fileName", files.get(pos).GetFileName());
                ctx.startService(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void addItem(ArrayList<File> files){
        this.files.addAll(0, files);
        notifyItemRangeInserted(0, this.files.size());
    }

    static class FileHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        TextView fileSize;
        TextView fileMD5;
        Button buttonDownload;

        FileHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.update_name);
            fileSize = itemView.findViewById(R.id.update_size);
            fileMD5 = itemView.findViewById(R.id.update_md5);
            buttonDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}