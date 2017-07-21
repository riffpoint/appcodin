package com.rgand.x_prt.lastfmhits.model.album;

import com.google.gson.annotations.SerializedName;

/**
 * Created by x_prt on 21.04.2017
 */

public class AlbumImageModel {

    @SerializedName("#text")
    private String imageUrl;
    private String size;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSize() {
        return size;
    }
}
