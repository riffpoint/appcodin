package com.rgand.x_prt.lastfmhits.model.artist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by x_prt on 21.04.2017
 */

public class TopArtistsModel {

    @SerializedName("artist")
    private List<ArtistModel> artistModelList;

    public List<ArtistModel> getArtistModelList() {
        return artistModelList;
    }
}
