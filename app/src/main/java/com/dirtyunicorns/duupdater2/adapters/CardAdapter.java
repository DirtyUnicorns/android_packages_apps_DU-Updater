package com.dirtyunicorns.duupdater2.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dirtyunicorns.duupdater2.R;
import com.dirtyunicorns.duupdater2.utils.File;

import java.util.ArrayList;

/**
 * Created by mazwoz on 7/5/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.FileHolder>{

    ArrayList<File> files;

    public CardAdapter(ArrayList<File> files) {
        this.files = files;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cardview, parent, false);
        FileHolder fh = new FileHolder(v);
        return fh;
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        holder.fileName.setText(files.get(position).GetFileName());
        holder.fileSize.setText("File Size: " + files.get(position).GetFileSize());
        holder.fileMD5.setText("File MD5: ");
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

        FileHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            fileName = (TextView) itemView.findViewById(R.id.update_name);
            fileSize = (TextView) itemView.findViewById(R.id.update_size);
            fileMD5 = (TextView) itemView.findViewById(R.id.update_md5);
        }
    }


}
