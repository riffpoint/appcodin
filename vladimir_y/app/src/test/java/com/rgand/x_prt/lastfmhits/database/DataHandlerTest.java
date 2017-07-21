package com.rgand.x_prt.lastfmhits.database;

import com.rgand.x_prt.lastfmhits.model.album.AlbumModel;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rgand.x_prt.lastfmhits.database.TestConstants.COUNTRY_1;
import static com.rgand.x_prt.lastfmhits.database.TestConstants.COUNTRY_2;
import static com.rgand.x_prt.lastfmhits.database.TestConstants.COUNTRY_3;
import static com.rgand.x_prt.lastfmhits.database.TestConstants.FIRST_ELEMENT_INDEX;
import static com.rgand.x_prt.lastfmhits.database.TestConstants.MOCK_ARTIST_NAME;
import static com.rgand.x_prt.lastfmhits.database.TestConstants.NUMBER_OF_MOCK_ARTISTS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by x_prt on 29.05.2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 24, manifest = Config.NONE)
public class DataHandlerTest {

    private static final String[] COUNTRIES = {COUNTRY_1, COUNTRY_2, COUNTRY_3};

    private DataHandler dataHandler;

    private AlbumModel mockAlbum;
    private Map<String, List<ArtistModel>> checkMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        dataHandler = new DataHandler(RuntimeEnvironment.application.getApplicationContext());
        dataHandler.open();

        saveArtistList();
        removeAllArtists();

        createMockAlbumModel();
        isAlbumSavedCorrectly();
    }

    @Test
    public void saveArtistList() {
        for (String countryName : COUNTRIES) {
            dataHandler.saveArtistList(createMockArtistModelList(countryName), countryName);
        }
        for (String countryName : COUNTRIES) {
            ArrayList<ArtistModel> list = dataHandler.getArtistList(countryName);
            assertThat(list, is(checkMap.get(countryName)));
        }
    }

    @Test
    public void removeAllArtists() {
        for (String countryName : COUNTRIES) {
            dataHandler.removeAllArtists(countryName);
            assertThat(dataHandler.getAlbumList(countryName), is(Collections.EMPTY_LIST));
        }
    }

    @Test
    public void isAlbumSavedCorrectly() {
        dataHandler.saveAlbumToDB(mockAlbum, MOCK_ARTIST_NAME);
        assertEquals(dataHandler.getAlbumList(MOCK_ARTIST_NAME).get(FIRST_ELEMENT_INDEX), mockAlbum);
    }

    @After
    public void closeTest() {
        dataHandler.removeAllAlbums(MOCK_ARTIST_NAME);
        dataHandler.close();
        checkMap.clear();
        mockAlbum = null;
    }


    private List<ArtistModel> createMockArtistModelList(String countryName) {
        List<ArtistModel> tempList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_MOCK_ARTISTS; i++) {
            ArtistModel artistModel = new ArtistModel();
            artistModel.setName("name" + System.currentTimeMillis());
            artistModel.setListeners("listeners" + System.currentTimeMillis());
            artistModel.setLargeImageUrl("http://largeImageUrl/" + System.currentTimeMillis());
            artistModel.setLargePhotoFilePath("largePhotoFilePath/" + System.currentTimeMillis());
            artistModel.setMegaImageUrl("http://megaImageUrl/" + System.currentTimeMillis());
            artistModel.setMegaPhotoFilePath("megaPhotoFilePath/" + System.currentTimeMillis());

            tempList.add(artistModel);
        }
        checkMap.put(countryName, tempList);
        return tempList;
    }

    private AlbumModel createMockAlbumModel() {
        AlbumModel tempModel = new AlbumModel();
        tempModel.setName("name" + System.currentTimeMillis());
        tempModel.setPlaycount("playcount" + System.currentTimeMillis());
        tempModel.setUrl("http://url/" + System.currentTimeMillis());
        tempModel.setLargeImageUrl("http://largeImageUrl/" + System.currentTimeMillis());
        tempModel.setPhotoFilePath("photoFilePath/" + System.currentTimeMillis());

        mockAlbum = tempModel;
        return tempModel;
    }
}