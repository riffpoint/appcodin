package com.rgand.x_prt.lastfmhits.util;

import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;

import java.util.Comparator;

/**
 * Created by x_prt on 24.04.2017
 */

public class ArtistByNameComparator implements Comparator<ArtistModel> {
    @Override
    public int compare(ArtistModel o1, ArtistModel o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
