package com.rgand.x_prt.lastfmhits.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rgand.x_prt.lastfmhits.R;
import com.rgand.x_prt.lastfmhits.listener.OnAlbumItemClickListener;
import com.rgand.x_prt.lastfmhits.model.album.AlbumModel;
import com.rgand.x_prt.lastfmhits.util.NumberFormatter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.TOP_ALBUM_RV_PHOTO_SIZE;

/**
 * Created by x_prt on 23.04.2017
 */

public class TopAlbumRVAdapter extends RecyclerView.Adapter<TopAlbumRVAdapter.AlbumViewHolder> {

    private OnAlbumItemClickListener onAlbumItemClickListener;

    private List<AlbumModel> albumModelList = new ArrayList<>();

    public TopAlbumRVAdapter(OnAlbumItemClickListener onAlbumItemClickListener) {
        this.onAlbumItemClickListener = onAlbumItemClickListener;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(itemView);
    }

    public void setList(List<AlbumModel> albumModelList) {
        this.albumModelList = albumModelList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        if (!albumModelList.isEmpty()) {
            AlbumModel model = albumModelList.get(position);
            holder.bind(model);
        }
    }

    //I didn't have enough time to create empty Recycler placeholder so I decided to show blanks
    //for albums witch have to been loaded
    @Override
    public int getItemCount() {
        return albumModelList.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivArtistPhoto;
        private TextView tvAlbumName;
        private TextView tvPlays;

        AlbumViewHolder(View itemView) {
            super(itemView);
            ivArtistPhoto = (ImageView) itemView.findViewById(R.id.iv_album_photo);
            tvAlbumName = (TextView) itemView.findViewById(R.id.tv_album_name);
            tvPlays = (TextView) itemView.findViewById(R.id.tv_plays);
        }

        void bind(final AlbumModel model) {
            Picasso.with(itemView.getContext())
                    .load(new File(model.getPhotoFilePath()))
                    .resize(TOP_ALBUM_RV_PHOTO_SIZE, TOP_ALBUM_RV_PHOTO_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.no_image_placeholder)
                    .error(R.drawable.no_image_placeholder)
                    .into(ivArtistPhoto);
            tvAlbumName.setText(model.getName() != null
                    ? model.getName() : itemView.getContext().getString(R.string.unknown_txt));

            String numOfListeners = NumberFormatter.spacesForBigNumber(model.getPlaycount())
                    + " "
                    + itemView.getContext().getString(R.string.plays_suffix_txt);
            tvPlays.setText(numOfListeners);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAlbumItemClickListener.onAlbumClicked(model.getUrl());
                }
            });
        }
    }
}
