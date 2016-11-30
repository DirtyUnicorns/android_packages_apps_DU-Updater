package com.dirtyunicorns.duupdater.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
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

import static java.lang.Thread.sleep;

/**
 * Created by mazwoz on 7/5/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.FileHolder>{

    private ArrayList<File> files;
    private Context ctx;
    private Intent intent;

    public CardAdapter(ArrayList<File> files, Context ctx) {
        this.files = files;
        this.ctx = ctx;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cardview, parent, false);
        FileHolder fh = new FileHolder(v);

        final TextView downloadFile = (TextView) v.findViewById(R.id.download_path);
        final TextView fileName = (TextView) v.findViewById(R.id.update_name);
        Button btnDownload = (Button) v.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = downloadFile.getText().toString();
                intent = new Intent(ctx, DownloadService.class);
                intent.putExtra("url", link);
                intent.putExtra("fileName", fileName.getText());
                ctx.startService(intent);

            }
        });

        return fh;
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        holder.fileName.setText(files.get(position).GetFileName());
        holder.fileSize.setText("File Size: " + files.get(position).GetFileSize());
        holder.fileMD5.setText("File MD5: " + files.get(position).GetFileMD5());
        holder.fildDownload.setText(files.get(position).GetFileLink());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class FileHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView fileName;
        TextView fileSize;
        TextView fileMD5;
        TextView fildDownload;

        FileHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            fileName = (TextView) itemView.findViewById(R.id.update_name);
            fileSize = (TextView) itemView.findViewById(R.id.update_size);
            fileMD5 = (TextView) itemView.findViewById(R.id.update_md5);
            fildDownload = (TextView) itemView.findViewById(R.id.download_path);
        }
    }
}
