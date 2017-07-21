package com.rgand.x_prt.lastfmhits.util;

import com.rgand.x_prt.lastfmhits.model.album.AlbumModel;

import java.util.Comparator;

/**
 * Created by x_prt on 24.04.2017
 */

public class AlbumByNameComparator implements Comparator<AlbumModel> {
    @Override
    public int compare(AlbumModel o1, AlbumModel o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
