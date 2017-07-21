package com.rgand.x_prt.lastfmhits.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rgand.x_prt.lastfmhits.R;
import com.rgand.x_prt.lastfmhits.adapter.TopArtistRVAdapter;
import com.rgand.x_prt.lastfmhits.base.BaseActivity;
import com.rgand.x_prt.lastfmhits.database.DataHandler;
import com.rgand.x_prt.lastfmhits.database.task.SaveArtistTask;
import com.rgand.x_prt.lastfmhits.database.task.SaveMegaArtistPhotoTask;
import com.rgand.x_prt.lastfmhits.listener.OnArtistItemClickListener;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;
import com.rgand.x_prt.lastfmhits.model.artist.GeoArtistData;
import com.rgand.x_prt.lastfmhits.network.listener.RequestListener;
import com.rgand.x_prt.lastfmhits.network.requests.GetArtistsRequest;
import com.rgand.x_prt.lastfmhits.util.AppConstants;
import com.rgand.x_prt.lastfmhits.util.ArtistByListenersComparator;
import com.rgand.x_prt.lastfmhits.util.ArtistByNameComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopularArtistActivity extends BaseActivity implements View.OnClickListener,
        OnArtistItemClickListener, SwipeRefreshLayout.OnRefreshListener, SaveArtistTask.OnTaskFinishedListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyPlaceholder;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AlertDialog selectLocationDialog;

    private boolean isSwipeRefreshing;
    private boolean isSortingByListeners;
    private String chosenCountry;
    private TopArtistRVAdapter artistRVAdapter;
    private List<ArtistModel> artistModelList = new ArrayList<>();

    private DataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        overridePendingTransition(R.anim.slide_down_in_animation, R.anim.slide_up_out_animation);

        dataHandler = new DataHandler(this);

        initViews();
        getTopArtistsRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataHandler.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        artistModelList = dataHandler.getArtistList(chosenCountry);
        artistRVAdapter.setList(sortArtistList(artistModelList));

        setEmptyPlaceholderVisibility();
        checkInternetConnection(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataHandler.close();
    }

    private void initViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_popular_activity));
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                AppConstants.TOP_ARTISTS_RV_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);

        chosenCountry = getResources().getString(R.string.country_ukraine_txt);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout_popular_activity);
        collapsingToolbarLayout.setTitle(chosenCountry);

        artistRVAdapter = new TopArtistRVAdapter(this);
        RecyclerView rvArtists = (RecyclerView) findViewById(R.id.rv_top_artists);
        rvArtists.setItemAnimator(new DefaultItemAnimator());
        rvArtists.setLayoutManager(layoutManager);
        rvArtists.setAdapter(artistRVAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocationDialog();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.site_red);

        emptyPlaceholder = (TextView) findViewById(R.id.empty_view_placeholder);
    }

    private void setEmptyPlaceholderVisibility() {
        if (artistModelList.isEmpty()) {
            emptyPlaceholder.setVisibility(View.VISIBLE);
        } else {
            emptyPlaceholder.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artists, menu);
        return true;
    }

    /**
     * "Feel free to take a creative freedom and show your design skills" in task description was written,
     * so I decided to make sorting at that menu and to and to move country's selector to FloatingActionButton
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!swipeRefreshLayout.isRefreshing()) {
            switch (item.getItemId()) {
                case R.id.action_sort_names:
                    isSortingByListeners = false;
                    artistRVAdapter.setList(sortArtistList(artistModelList));
                    break;
                case R.id.action_sort_listeners:
                    isSortingByListeners = true;
                    artistRVAdapter.setList(sortArtistList(artistModelList));
                    break;
                default:
            }
        } else {
            showSnackMessage(getString(R.string.please_wait_txt));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * request to API for getting top artists of chosen (hardcoded) country
     */
    private void getTopArtistsRequest() {
        checkInternetConnection(PopularArtistActivity.this);

        if (!isSwipeRefreshing) {
            showProgressBar();
        } else {
            isSwipeRefreshing = false;
        }

        GetArtistsRequest getArtistsRequest = new GetArtistsRequest(chosenCountry);
        getArtistsRequest.setListener(new RequestListener<GeoArtistData>() {
            @Override
            public void onSuccess(GeoArtistData result) {
                artistModelList = result.getTopartists().getArtistModelList();
                artistRVAdapter.setList(sortArtistList(artistModelList));
                setEmptyPlaceholderVisibility();

                SaveArtistTask artistTask = new SaveArtistTask(
                        PopularArtistActivity.this,
                        PopularArtistActivity.this,
                        artistModelList,
                        chosenCountry
                );
                artistTask.execute();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgressBar();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getArtistsRequest.execute();
    }

    private List<ArtistModel> sortArtistList(List<ArtistModel> artistModelList) {
        Collections.sort(artistModelList, isSortingByListeners
                ? new ArtistByListenersComparator() : new ArtistByNameComparator());
        return artistModelList;
    }

    /**
     * here must be GoogleMaps screen calling or GooglePlaces search tool
     */
    private void showLocationDialog() {
        View view = View.inflate(this, R.layout.dialog_select_location, null);
        if (selectLocationDialog == null) {
            selectLocationDialog = new AlertDialog.Builder(this).create();
            selectLocationDialog.setView(view);
            selectLocationDialog.setCancelable(true);
            if (selectLocationDialog.getWindow() != null) {
                selectLocationDialog.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            view.findViewById(R.id.tv_dialog_location_ukraine).setOnClickListener(this);
            view.findViewById(R.id.tv_dialog_location_georgia).setOnClickListener(this);
            view.findViewById(R.id.tv_dialog_location_switzerland).setOnClickListener(this);
        }
        selectLocationDialog.dismiss();
        selectLocationDialog.show();
    }

    @Override
    public void onClick(View v) {
        selectLocationDialog.dismiss();
        switch (v.getId()) {
            case R.id.tv_dialog_location_ukraine:
                chosenCountry = getResources().getString(R.string.country_ukraine_txt);
                refreshRecyclerView();
                break;
            case R.id.tv_dialog_location_georgia:
                chosenCountry = getResources().getString(R.string.country_georgia_txt);
                refreshRecyclerView();
                break;
            case R.id.tv_dialog_location_switzerland:
                chosenCountry = getResources().getString(R.string.country_switzerland_txt);
                refreshRecyclerView();
                break;
            default:
        }
    }

    private void refreshRecyclerView() {
        artistModelList = dataHandler.getArtistList(chosenCountry);
        artistRVAdapter.setList(sortArtistList(artistModelList));
        collapsingToolbarLayout.setTitle(chosenCountry);
        setEmptyPlaceholderVisibility();

        getTopArtistsRequest();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up_in_animation, R.anim.slide_down_out_animation);
    }

    @Override
    public void onArtistItemClicked(String artistName) {
        Intent intent = new Intent(PopularArtistActivity.this, ArtistInfoActivity.class);
        intent.putExtra(ArtistInfoActivity.ARTIST_KEY, artistName);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        isSwipeRefreshing = true;
        refreshRecyclerView();
    }

    @Override
    public void onDataLoaded(List<ArtistModel> list) {
        artistRVAdapter.setList(sortArtistList(list));

        hideProgressBar();
        swipeRefreshLayout.setRefreshing(false);

        SaveMegaArtistPhotoTask smapt = new SaveMegaArtistPhotoTask(
                this,
                chosenCountry);
        smapt.execute();
    }
}
