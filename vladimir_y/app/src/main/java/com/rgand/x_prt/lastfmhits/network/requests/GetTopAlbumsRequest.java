package com.rgand.x_prt.lastfmhits.network.requests;

import com.rgand.x_prt.lastfmhits.model.album.ArtistInfoData;
import com.rgand.x_prt.lastfmhits.network.NetworkHelper;

import retrofit2.Call;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.TOP_ITEMS_COUNT;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.TOP_ITEMS_PAGING;

/**
 * Created by x_prt on 22.04.2017
 */

public class GetTopAlbumsRequest extends BaseRequest<ArtistInfoData> {

    private final static String CALLING_METHOD = "artist.getTopAlbums";

    private String artist;
    private String albumsPerPage;
    private String pageNumber;

    /**
     * At this example decided to show only TOP-10 of albums for each singer.
     * That's why artistsPerPage equals TOP_ITEMS_COUNT and page number is TOP_ITEMS_PAGING.
     *
     * @param artist - the artist which was selected from TOP-list
     */
    public GetTopAlbumsRequest(String artist) {
        this.artist = artist;
        this.albumsPerPage = String.valueOf(TOP_ITEMS_COUNT);
        this.pageNumber = String.valueOf(TOP_ITEMS_PAGING);
    }

    @Override
    public Call<ArtistInfoData> getCall() {
        return NetworkHelper.getInstance().getAppService().getTopAlbums(
                CALLING_METHOD,
                artist,
                albumsPerPage,
                pageNumber);
    }
}
