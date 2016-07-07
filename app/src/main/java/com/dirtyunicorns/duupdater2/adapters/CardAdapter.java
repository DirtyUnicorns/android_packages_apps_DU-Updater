package com.dirtyunicorns.duupdater2.adapters;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.PowerManager;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dirtyunicorns.duupdater2.R;
import com.dirtyunicorns.duupdater2.services.DownloadService;
import com.dirtyunicorns.duupdater2.utils.File;

import java.util.ArrayList;

/**
 * Created by mazwoz on 7/5/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.FileHolder>{

    private ArrayList<File> files;
    private Context ctx;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private Intent intent;
    private int resCode;
    private Bundle resData;
    private DownloadReceiver dlRec;

    public CardAdapter(ArrayList<File> files, Context ctx) {
        this.files = files;
        this.ctx = ctx;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cardview, parent, false);
        FileHolder fh = new FileHolder(v);

        dlRec = new DownloadReceiver(new Handler());

        final TextView downloadFile = (TextView) v.findViewById(R.id.download_path);
        final TextView fileName = (TextView) v.findViewById(R.id.update_name);
        Button btnDownload = (Button) v.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = downloadFile.getText().toString();


                mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(ctx);
                mBuilder.setContentTitle("Dirty Unicorns")
                        .setContentText("Downloading File")
                        .setSmallIcon(android.R.drawable.stat_sys_download);
                intent = new Intent(ctx, DownloadService.class);
                intent.putExtra("url", link);
                intent.putExtra("fileName", fileName.getText());
                intent.putExtra("receiver", dlRec);


                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                ctx.startService(intent);
                            }
                        }
                ).start();
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

    @SuppressLint("ParcelCreator")
    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver (Handler parcel) {
            super(parcel);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            resCode = resultCode;
            resData = resultData;

            System.out.println("Progress of download: " + resultData.getInt("progress"));

            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                Thread download = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                int progress = resData.getInt("progress");
                                if (progress < 100) {
                                    mBuilder.setProgress(100, progress, false);
                                    mBuilder.setContentText(progress + "/100");
                                    mNotifyManager.notify(resCode, mBuilder.build());
                                }

                            }
                        }
                );
                download.start();
                while (download.isAlive()) {

                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        Log.d("DirtyUnicornsUpdater", "Sleep Failure");
                    }
                }
                if (!download.isAlive()) {
                    /*mBuilder.setContentText("Download Completed!");
                    mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                    mBuilder.setProgress(0,0,false);
                    mNotifyManager.notify(resultCode, mBuilder.build());*/
                }
            }

        }
    }
}
