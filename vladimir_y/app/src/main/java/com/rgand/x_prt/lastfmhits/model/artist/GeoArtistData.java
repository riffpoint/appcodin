package com.rgand.x_prt.lastfmhits.model.artist;

import com.google.gson.annotations.SerializedName;

/**
 * Created by x_prt on 22.04.2017
 */

public class GeoArtistData {

    @SerializedName("topartists")
    private TopArtistsModel topartists;

    public TopArtistsModel getTopartists() {
        return topartists;
    }
}
