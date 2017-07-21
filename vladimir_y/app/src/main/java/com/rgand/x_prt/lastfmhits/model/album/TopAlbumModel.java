package com.rgand.x_prt.lastfmhits.model.album;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by x_prt on 21.04.2017
 */

public class TopAlbumModel {

    @SerializedName("album")
    private List<AlbumModel> albumModels;

    public List<AlbumModel> getAlbumModelList() {
        return albumModels;
    }
}
