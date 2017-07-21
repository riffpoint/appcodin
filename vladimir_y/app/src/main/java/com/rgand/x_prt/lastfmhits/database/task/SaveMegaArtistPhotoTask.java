package com.rgand.x_prt.lastfmhits.database.task;

import android.content.Context;
import android.os.AsyncTask;

import com.rgand.x_prt.lastfmhits.database.DataHandler;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;
import com.rgand.x_prt.lastfmhits.util.FileUtils;

import java.util.List;

/**
 * Created by x_prt on 10.05.2017
 */

public class SaveMegaArtistPhotoTask extends AsyncTask<Void, Void, List<ArtistModel>> {

    private Context context;
    private DataHandler dataHandler;

    private List<ArtistModel> boostedList;
    private String artistCountry;

    public SaveMegaArtistPhotoTask(Context context, String artistCountry) {
        this.context = context;

        dataHandler = new DataHandler(context);
        dataHandler.open();

        this.artistCountry = artistCountry;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.boostedList = dataHandler.getArtistList(artistCountry);
    }

    @Override
    protected List<ArtistModel> doInBackground(Void... params) {
        for (ArtistModel artist : boostedList) {
            FileUtils.saveFilepath(context, artist, artistCountry, artist.getMegaImageUrl(), true);
        }
        dataHandler.saveArtistList(boostedList, artistCountry);
        dataHandler.close();
        return boostedList;
    }
}
