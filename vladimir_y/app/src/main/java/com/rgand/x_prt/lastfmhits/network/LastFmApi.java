package com.rgand.x_prt.lastfmhits.network;

import com.rgand.x_prt.lastfmhits.model.album.ArtistInfoData;
import com.rgand.x_prt.lastfmhits.model.artist.GeoArtistData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by x_prt on 21.04.2017
 */

public interface LastFmApi {

    /**
     * @param callingMethod  - API method
     * @param chosenCountry  - country selected by the user from dialog (hardcoded)
     * @param artistsPerPage - number of requested artists
     * @param pageNumber     - number of page (for pagination)
     */
    @GET("2.0/")
    Call<GeoArtistData> getTopArtists(
            @Query("method") String callingMethod,
            @Query("country") String chosenCountry,
            @Query("limit") String artistsPerPage,
            @Query("page") String pageNumber);

    /**
     * @param callingMethod - API method
     * @param artist - artist selected by the user from list
     * @param albumsPerPage - number of requested albums
     * @param pageNumber - number of page (for pagination)
     */
    @GET("2.0/")
    Call<ArtistInfoData> getTopAlbums(
            @Query("method") String callingMethod,
            @Query("artist") String artist,
            @Query("limit") String albumsPerPage,
            @Query("page") String pageNumber);
}
