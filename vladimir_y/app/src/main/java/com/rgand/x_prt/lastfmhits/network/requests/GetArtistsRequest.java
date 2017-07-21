package com.rgand.x_prt.lastfmhits.network.requests;

import com.rgand.x_prt.lastfmhits.model.artist.GeoArtistData;
import com.rgand.x_prt.lastfmhits.network.NetworkHelper;

import retrofit2.Call;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.TOP_ITEMS_COUNT;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.TOP_ITEMS_PAGING;

/**
 * Created by x_prt on 21.04.2017
 */

public class GetArtistsRequest extends BaseRequest<GeoArtistData> {

    private final static String CALLING_METHOD = "geo.getTopArtists";

    private String chosenCountry;
    private String artistsPerPage;
    private String pageNumber;

    /**
     * At this example decided to show only TOP-10 of artists for each country.
     * That's why artistsPerPage equals TOP_ITEMS_COUNT and page number is TOP_ITEMS_PAGING.
     *
     * @param chosenCountry - one of three hardcoded country from menu at UI
     */
    public GetArtistsRequest(String chosenCountry) {
        this.chosenCountry = chosenCountry;
        this.artistsPerPage = String.valueOf(TOP_ITEMS_COUNT);
        this.pageNumber = String.valueOf(TOP_ITEMS_PAGING);
    }

    @Override
    public Call<GeoArtistData> getCall() {
        return NetworkHelper.getInstance().getAppService().getTopArtists(
                CALLING_METHOD,
                chosenCountry,
                artistsPerPage,
                pageNumber);
    }
}
