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

/**
 * Created by mazwoz on 7/5/16.
 */
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
        holder.fildDownload.setText(files.get(pos).GetFileLink());
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
        TextView fildDownload;
        Button buttonDownload;

        FileHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.update_name);
            fileSize = (TextView) itemView.findViewById(R.id.update_size);
            fileMD5 = (TextView) itemView.findViewById(R.id.update_md5);
            fildDownload = (TextView) itemView.findViewById(R.id.download_path);
            buttonDownload = (Button) itemView.findViewById(R.id.btnDownload);
        }
    }
}
