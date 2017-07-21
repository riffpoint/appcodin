package com.rgand.x_prt.lastfmhits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.rgand.x_prt.lastfmhits.model.album.AlbumModel;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;

import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    private static final String DATA_BASE_NAME = "lfm_database";
    private static final String ALBUMS_TABLE = "albums_table";
    private static final String ARTISTS_TABLE = "artists_table";
    private static final int DATABASE_VERSION = 1;

    private static final String ARTIST_COUNTRY = "artist_country";
    private static final String ARTIST_NAME = "artist_name";
    private static final String ARTIST_LISTENERS = "artist_listeners";
    private static final String ARTIST_LARGE_PHOTO = "artist_large_image_url";
    private static final String ARTIST_LARGE_PHOTO_PATH = "artist_large_photo_path";
    private static final String ARTIST_MEGA_PHOTO = "artist_mega_image_url";
    private static final String ARTIST_MEGA_PHOTO_PATH = "artist_mega_photo_path";

    private static final String ALBUM_NAME = "album_name";
    private static final String ALBUM_PLAYCOUNT = "album_playcount";
    private static final String ALBUM_URL = "album_url";
    private static final String ALBUM_LARGE_PHOTO = "album_large_image_url";
    private static final String ALBUM_PHOTO_PATH = "album_photo_path";

    private static final String ARTIST_SQL = "CREATE TABLE " +
            ARTISTS_TABLE +
            "(" +
            ARTIST_COUNTRY + " TEXT, " +
            ARTIST_NAME + " TEXT, " +
            ARTIST_LISTENERS + " TEXT, " +
            ARTIST_LARGE_PHOTO + " TEXT, " +
            ARTIST_LARGE_PHOTO_PATH + " TEXT, " +
            ARTIST_MEGA_PHOTO + " TEXT, " +
            ARTIST_MEGA_PHOTO_PATH + " TEXT" +
            ");";

    private static final String ALBUM_SQL = "CREATE TABLE " +
            ALBUMS_TABLE +
            "(" +
            ARTIST_NAME + " TEXT, " +
            ALBUM_NAME + " TEXT, " +
            ALBUM_PLAYCOUNT + " TEXT, " +
            ALBUM_URL + " TEXT, " +
            ALBUM_LARGE_PHOTO + " TEXT, " +
            ALBUM_PHOTO_PATH + " TEXT" +
            ");";

    private SQLiteDatabase sqLiteDB;
    private DataBaseHelper dbHelper;

    public DataHandler(Context context) {
        dbHelper = new DataBaseHelper(context);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {
        DataBaseHelper(Context context) {
            super(context, DATA_BASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ARTIST_SQL);
            db.execSQL(ALBUM_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL("DROP TABLE IF EXIST " + ARTISTS_TABLE);
            db.execSQL("DROP TABLE IF EXIST " + ALBUMS_TABLE);

            onCreate(db);
        }
    }

    public DataHandler open() {
        sqLiteDB = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void saveArtistList(List<ArtistModel> artistModelList, String country) {
        removeAllArtists(country);
        for (ArtistModel artistModel :
                artistModelList) {
            saveArtistToDB(artistModel, country);
        }
    }

    private void saveArtistToDB(ArtistModel artistModel, String country) {
        sqLiteDB.insert(ARTISTS_TABLE, null, getArtistContentValues(artistModel, country));
    }

    @NonNull
    private ContentValues getArtistContentValues(ArtistModel artistModel, String country) {
        ContentValues content = new ContentValues();
        content.put(ARTIST_COUNTRY, country);
        content.put(ARTIST_NAME, artistModel.getName());
        content.put(ARTIST_LISTENERS, artistModel.getListeners());
        content.put(ARTIST_LARGE_PHOTO, artistModel.getLargeImageUrl());
        content.put(ARTIST_LARGE_PHOTO_PATH, artistModel.getLargePhotoFilePath());
        content.put(ARTIST_MEGA_PHOTO, artistModel.getMegaImageUrl());
        content.put(ARTIST_MEGA_PHOTO_PATH, artistModel.getMegaPhotoFilePath());
        return content;
    }

    protected void removeAllArtists(String country) {
        String whereClause = ARTIST_COUNTRY + " = ?";
        String[] whereArgs = new String[]{country};
        sqLiteDB.delete(ARTISTS_TABLE, whereClause, whereArgs);
    }

    public ArrayList<ArtistModel> getArtistList(String country) {
        String whereClause = ARTIST_COUNTRY + " = ?";
        String[] whereArgs = new String[]{country};
        Cursor cursor = sqLiteDB.query(ARTISTS_TABLE, null, whereClause, whereArgs, null, null, null);

        ArrayList<ArtistModel> models = new ArrayList<ArtistModel>();
        while (cursor.moveToNext()) {
            models.add(createArtist(cursor));
        }
        cursor.close();
        return models;
    }

    public ArtistModel getArtistByName(String artistName) {
        String whereClause = ARTIST_NAME + " = ?";
        String[] whereArgs = new String[]{artistName};
        Cursor cursor = sqLiteDB.query(ARTISTS_TABLE, null, whereClause, whereArgs, null, null, null);
        cursor.moveToFirst();
        return createArtist(cursor);
    }

    private ArtistModel createArtist(Cursor cursor) {
        ArtistModel artistModel = new ArtistModel();

        artistModel.setName(cursor.getString(cursor.getColumnIndex(ARTIST_NAME)));
        artistModel.setListeners(cursor.getString(cursor.getColumnIndex(ARTIST_LISTENERS)));
        artistModel.setLargeImageUrl(cursor.getString(cursor.getColumnIndex(ARTIST_LARGE_PHOTO)));
        artistModel.setLargePhotoFilePath(cursor.getString(cursor.getColumnIndex(ARTIST_LARGE_PHOTO_PATH)));
        artistModel.setMegaImageUrl(cursor.getString(cursor.getColumnIndex(ARTIST_MEGA_PHOTO)));
        artistModel.setMegaPhotoFilePath(cursor.getString(cursor.getColumnIndex(ARTIST_MEGA_PHOTO_PATH)));

        return artistModel;
    }

    public void saveAlbumList(List<AlbumModel> albumModelArrayList, String artistName) {
        removeAllAlbums(artistName);
        for (AlbumModel album :
                albumModelArrayList) {
            saveAlbumToDB(album, artistName);
        }
    }

    protected void saveAlbumToDB(AlbumModel albumModel, String artistName) {
        sqLiteDB.insert(ALBUMS_TABLE, null, getAlbumContentValues(albumModel, artistName));
    }

    @NonNull
    private ContentValues getAlbumContentValues(AlbumModel albumModel, String artistName) {
        ContentValues content = new ContentValues();
        content.put(ARTIST_NAME, artistName);
        content.put(ALBUM_NAME, albumModel.getName());
        content.put(ALBUM_PLAYCOUNT, albumModel.getPlaycount());
        content.put(ALBUM_URL, albumModel.getUrl());
        content.put(ALBUM_LARGE_PHOTO, albumModel.getLargeImageUrl());
        content.put(ALBUM_PHOTO_PATH, albumModel.getPhotoFilePath());
        return content;
    }

    protected void removeAllAlbums(String artistName) {
        String whereClause = ARTIST_NAME + " = ?";
        String[] whereArgs = new String[]{artistName};
        sqLiteDB.delete(ALBUMS_TABLE, whereClause, whereArgs);
    }

    public ArrayList<AlbumModel> getAlbumList(String artistName) {
        String whereClause = ARTIST_NAME + " = ?";
        String[] whereArgs = new String[]{artistName};
        Cursor cursor = sqLiteDB.query(ALBUMS_TABLE, null, whereClause, whereArgs, null, null, null);

        ArrayList<AlbumModel> models = new ArrayList<AlbumModel>();
        while (cursor.moveToNext()) {
            models.add(createAlbum(cursor));
        }
        cursor.close();
        return models;
    }

    private AlbumModel createAlbum(Cursor cursor) {
        AlbumModel albumModel = new AlbumModel();

        albumModel.setName(cursor.getString(cursor.getColumnIndex(ALBUM_NAME)));
        albumModel.setPlaycount(cursor.getString(cursor.getColumnIndex(ALBUM_PLAYCOUNT)));
        albumModel.setUrl(cursor.getString(cursor.getColumnIndex(ALBUM_URL)));
        albumModel.setLargeImageUrl(cursor.getString(cursor.getColumnIndex(ALBUM_LARGE_PHOTO)));
        albumModel.setPhotoFilePath(cursor.getString(cursor.getColumnIndex(ALBUM_PHOTO_PATH)));

        return albumModel;
    }
}

