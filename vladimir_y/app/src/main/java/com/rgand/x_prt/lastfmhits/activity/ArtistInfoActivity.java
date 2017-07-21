package com.rgand.x_prt.lastfmhits.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rgand.x_prt.lastfmhits.R;
import com.rgand.x_prt.lastfmhits.adapter.TopAlbumRVAdapter;
import com.rgand.x_prt.lastfmhits.base.BaseActivity;
import com.rgand.x_prt.lastfmhits.database.DataHandler;
import com.rgand.x_prt.lastfmhits.database.task.SaveAlbumTask;
import com.rgand.x_prt.lastfmhits.listener.OnAlbumItemClickListener;
import com.rgand.x_prt.lastfmhits.model.album.AlbumModel;
import com.rgand.x_prt.lastfmhits.model.album.ArtistInfoData;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;
import com.rgand.x_prt.lastfmhits.network.listener.RequestListener;
import com.rgand.x_prt.lastfmhits.network.requests.GetTopAlbumsRequest;
import com.rgand.x_prt.lastfmhits.util.AlbumByNameComparator;
import com.rgand.x_prt.lastfmhits.util.AlbumByPlayCountComparator;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.TOP_ARTISTS_RV_PHOTO_SIZE;

public class ArtistInfoActivity extends BaseActivity implements OnAlbumItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, SaveAlbumTask.OnTaskFinishedListener {

    public static final String ARTIST_KEY = "chosen_artist";

    private boolean isSwipeRefreshing;
    private boolean isSortingByPlaycount;
    private List<AlbumModel> albumModelList = new ArrayList<>();
    private String chosenArtistName;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyPlaceholder;
    private TopAlbumRVAdapter infoRVAdapter;
    private ImageView ivArtistPhoto;

    private DataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);
        overridePendingTransition(R.anim.slide_down_in_animation, R.anim.slide_up_out_animation);

        chosenArtistName = getIntent().getStringExtra(ARTIST_KEY);

        dataHandler = new DataHandler(this);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataHandler.open();
        refreshArtistPhoto();

        albumModelList = dataHandler.getAlbumList(chosenArtistName);
        infoRVAdapter.setList(sortAlbumList(albumModelList));

        setEmptyPlaceholderVisibility();
        getTopAlbumsRequest();
        checkInternetConnection(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataHandler.close();
    }

    private void initViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_info_activity));

        CollapsingToolbarLayout collapsingToolbarLayout
                = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout_info_activity);
        collapsingToolbarLayout.setTitle(chosenArtistName);

        ivArtistPhoto = (ImageView) collapsingToolbarLayout.findViewById(R.id.iv_artist_info_photo);
        ivArtistPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshArtistPhoto();
            }
        });

        infoRVAdapter = new TopAlbumRVAdapter(this);
        RecyclerView rvArtists = (RecyclerView) findViewById(R.id.rv_top_artists);
        rvArtists.setItemAnimator(new DefaultItemAnimator());
        rvArtists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvArtists.setAdapter(infoRVAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.site_red);

        emptyPlaceholder = (TextView) findViewById(R.id.empty_view_placeholder);
    }

    private void setEmptyPlaceholderVisibility() {
        if (albumModelList.isEmpty()) {
            emptyPlaceholder.setVisibility(View.VISIBLE);
        } else {
            emptyPlaceholder.setVisibility(View.GONE);
        }
    }

    private void refreshArtistPhoto() {
        ArtistModel chosenArtist = dataHandler.getArtistByName(chosenArtistName);
        Picasso.with(this)
                .load(new File(chosenArtist.getMegaPhotoFilePath()))
                .resize(TOP_ARTISTS_RV_PHOTO_SIZE, TOP_ARTISTS_RV_PHOTO_SIZE)
                .centerCrop()
                .placeholder(R.drawable.atrist_image_placeholder)
                .error(R.drawable.atrist_image_placeholder)
                .into(ivArtistPhoto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_albums, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!swipeRefreshLayout.isRefreshing()) {
            switch (item.getItemId()) {
                case R.id.action_sort_names:
                    isSortingByPlaycount = false;
                    infoRVAdapter.setList(sortAlbumList(albumModelList));
                    break;
                case R.id.action_sort_playcount:
                    isSortingByPlaycount = true;
                    infoRVAdapter.setList(sortAlbumList(albumModelList));
                    break;
                default:
            }
        } else {
            showSnackMessage(getString(R.string.please_wait_txt));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * request to API for getting top albums of chosen artist
     */
    private void getTopAlbumsRequest() {
        checkInternetConnection(ArtistInfoActivity.this);
        if (!isSwipeRefreshing) {
            showProgressBar();
        } else {
            isSwipeRefreshing = false;
        }

        GetTopAlbumsRequest getTopAlbumsRequest = new GetTopAlbumsRequest(chosenArtistName);
        getTopAlbumsRequest.setListener(new RequestListener<ArtistInfoData>() {
            @Override
            public void onSuccess(ArtistInfoData result) {
                albumModelList = result.getTopAlbums().getAlbumModelList();
                infoRVAdapter.setList(sortAlbumList(albumModelList));
                setEmptyPlaceholderVisibility();

                SaveAlbumTask albumTask = new SaveAlbumTask(
                        ArtistInfoActivity.this,
                        ArtistInfoActivity.this,
                        albumModelList,
                        chosenArtistName);
                albumTask.execute();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgressBar();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getTopAlbumsRequest.execute();
    }

    private List<AlbumModel> sortAlbumList(List<AlbumModel> albumModels) {
        Collections.sort(albumModels, isSortingByPlaycount
                ? new AlbumByPlayCountComparator() : new AlbumByNameComparator());
        return albumModels;
    }

    /**
     * The test task does not imply a transition to the screen with the songs of the selected album,
     * but I decided to do, at least, the transition to the album page on the www.last.fm
     *
     * @param albumPublicUrl - public link to the album's page
     */
    @Override
    public void onAlbumClicked(String albumPublicUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(albumPublicUrl));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up_in_animation, R.anim.slide_down_out_animation);
    }

    @Override
    public void onRefresh() {
        isSwipeRefreshing = true;
        refreshArtistPhoto();
        getTopAlbumsRequest();
    }

    @Override
    public void onDataLoaded(List<AlbumModel> list) {
        infoRVAdapter.setList(sortAlbumList(list));
        setEmptyPlaceholderVisibility();

        hideProgressBar();
        swipeRefreshLayout.setRefreshing(false);
    }
}
