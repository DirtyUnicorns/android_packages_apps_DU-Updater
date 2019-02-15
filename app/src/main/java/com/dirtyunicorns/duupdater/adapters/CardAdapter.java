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

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dirtyunicorns.duupdater.MainActivity;
import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.utils.File;

import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.dirtyunicorns.duupdater.utils.InterfaceHelper.showSnackBar;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.FileHolder>{

    private ArrayList<File> files;
    private Context ctx;

    public CardAdapter(Context ctx){
        this.ctx = ctx;
        this.files = new ArrayList<>();
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cardview, parent, false);
        return new FileHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        holder.fileName.setText(files.get(position).GetFileName());
        holder.fileName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.fileName.setSelected(false);
                return false;
            }
        });

        holder.fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fileName.setSelected(true);
                showSnackBar(R.string.card_snackbar, MainActivity.snackbar);
            }
        });

        holder.fileSizeTitle.setText(R.string.card_file_size);
        holder.fileSizeSummary.setText(files.get(pos).GetFileSize());
        holder.fileMD5Title.setText(R.string.card_file_md5);
        holder.fileMD5Summary.setText(files.get(pos).GetFileMD5());
        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.Request request;
                request = new DownloadManager.Request(Uri.parse(files.get(pos).GetFileLink()));
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, files.get(pos).GetFileName());
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                DownloadManager manager = (DownloadManager) ctx.getSystemService(DOWNLOAD_SERVICE);
                if (manager != null) {
                    manager.enqueue(request);
                }
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                showSnackBar(R.string.download_starting_snackbar,MainActivity.snackbar);
            }
        });

        holder.fileMD5Title.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void clear() {
        final int size = files.size();
        if (size > 0) {
            files.subList(0, size).clear();
            notifyItemRangeRemoved(0, size);
            notifyDataSetChanged();
        }
    }

    public void addItem(ArrayList<File> files){
        this.files.addAll(0, files);
        notifyItemRangeInserted(0, this.files.size());
    }

    static class FileHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        TextView fileSizeTitle;
        TextView fileSizeSummary;
        TextView fileMD5Title;
        TextView fileMD5Summary;
        Button buttonDownload;

        FileHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.update_name);
            fileSizeTitle = itemView.findViewById(R.id.update_size_title);
            fileSizeSummary = itemView.findViewById(R.id.update_size_summary);
            fileMD5Title = itemView.findViewById(R.id.update_md5_title);
            fileMD5Summary = itemView.findViewById(R.id.update_md5_summary);
            buttonDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}