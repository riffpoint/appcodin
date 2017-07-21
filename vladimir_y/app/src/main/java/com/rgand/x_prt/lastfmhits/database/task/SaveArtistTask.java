package com.rgand.x_prt.lastfmhits.database.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.rgand.x_prt.lastfmhits.database.DataHandler;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistImageModel;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;
import com.rgand.x_prt.lastfmhits.util.FileUtils;

import java.util.List;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.IMAGE_MODEL_LARGE_KEY;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.IMAGE_MODEL_MEGA_KEY;

/**
 * Created by x_prt on 10.05.2017
 */

public class SaveArtistTask extends AsyncTask<Void, Void, List<ArtistModel>> {

    private Context context;
    private OnTaskFinishedListener listener;
    private DataHandler dataHandler;

    private List<ArtistModel> boostedList;
    private String artistCountry;

    public SaveArtistTask(Context context, OnTaskFinishedListener listener,
                          List<ArtistModel> boostedList, String artistCountry) {
        this.context = context;
        this.listener = listener;
        this.boostedList = boostedList;
        this.artistCountry = artistCountry;

        dataHandler = new DataHandler(context);
        dataHandler.open();
    }

    @Override
    protected List<ArtistModel> doInBackground(Void... params) {
        for (ArtistModel artist : boostedList) {
            for (ArtistImageModel image : artist.getImageModelList()) {
                if (image.getSize().equals(IMAGE_MODEL_LARGE_KEY)) {
                    if (image.getImageUrl() != null && !TextUtils.isEmpty(image.getImageUrl())) {
                        artist.setLargeImageUrl(image.getImageUrl());
                        FileUtils.saveFilepath(context, artist, artistCountry, image.getImageUrl(), false);
                    }
                }
                if (image.getSize().equals(IMAGE_MODEL_MEGA_KEY)) {
                    if (image.getImageUrl() != null && !TextUtils.isEmpty(image.getImageUrl())) {
                        artist.setMegaImageUrl(image.getImageUrl());
                    }
                }
            }
            dataHandler.saveArtistList(boostedList, artistCountry);
        }
        dataHandler.close();
        return boostedList;
    }

    @Override
    protected void onPostExecute(List<ArtistModel> list) {
        super.onPostExecute(list);
        listener.onDataLoaded(list);
    }

    public interface OnTaskFinishedListener {
        void onDataLoaded(List<ArtistModel> list);
    }
}
