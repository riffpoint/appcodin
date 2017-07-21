package com.rgand.x_prt.lastfmhits.database.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import com.rgand.x_prt.lastfmhits.database.DataHandler;
import com.rgand.x_prt.lastfmhits.model.album.AlbumImageModel;
import com.rgand.x_prt.lastfmhits.model.album.AlbumModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.APP_CASH_DIRECTORY;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.APP_CASH_FILE_NAME;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.IMAGE_MODEL_LARGE_KEY;

/**
 * Created by x_prt on 10.05.2017
 */

public class SaveAlbumTask extends AsyncTask<Void, Void, List<AlbumModel>> {

    private OnTaskFinishedListener listener;
    private DataHandler dataHandler;

    private List<AlbumModel> boostedList;
    private String artistName;

    public SaveAlbumTask(Context context, OnTaskFinishedListener listener,
                         List<AlbumModel> boostedList, String artistName) {
        this.listener = listener;
        this.boostedList = boostedList;
        this.artistName = artistName;

        dataHandler = new DataHandler(context);
        dataHandler.open();
    }

    @Override
    protected List<AlbumModel> doInBackground(Void... params) {
        for (AlbumModel album : boostedList) {

            for (AlbumImageModel image : album.getImageModelList()) {
                if (image.getSize().equals(IMAGE_MODEL_LARGE_KEY)) {
                    if (image.getImageUrl() != null && !TextUtils.isEmpty(image.getImageUrl())) {
                        album.setLargeImageUrl(image.getImageUrl());
                        saveFilepath(album, artistName, image.getImageUrl());
                    }
                }
            }
        }
        dataHandler.saveAlbumList(boostedList, artistName);
        dataHandler.close();
        return boostedList;
    }

    @Override
    protected void onPostExecute(List<AlbumModel> list) {
        super.onPostExecute(list);
        listener.onDataLoaded(list);
    }

    private void saveFilepath(AlbumModel album, final String artistName, String photoUrl) {
        try {

            URL url = new URL(photoUrl);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();

            File cashDirectory = new File(Environment.getExternalStorageDirectory().getPath()
                    + File.separator
                    + APP_CASH_DIRECTORY
                    + File.separator
                    + artistName.replaceAll("\\s", "")
                    + File.separator
                    + album.getName().replaceAll("\\s", ""));
            cashDirectory.mkdirs();

            String photoFilePath = cashDirectory
                    + File.separator
                    + APP_CASH_FILE_NAME;

            File tempFile = new File(photoFilePath);

            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buf = new byte[512];
            while (true) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                fos.write(buf, 0, len);
            }
            in.close();
            fos.flush();
            fos.close();

            album.setPhotoFilePath(photoFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface OnTaskFinishedListener {
        void onDataLoaded(List<AlbumModel> list);
    }
}
