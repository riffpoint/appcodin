package com.rgand.x_prt.lastfmhits.model.album;

import com.google.gson.annotations.SerializedName;

/**
 * Created by x_prt on 22.04.2017
 */

public class ArtistInfoData {

    @SerializedName("topalbums")
    private TopAlbumModel topAlbumModel;

    public TopAlbumModel getTopAlbums() {
        return topAlbumModel;
    }
}
