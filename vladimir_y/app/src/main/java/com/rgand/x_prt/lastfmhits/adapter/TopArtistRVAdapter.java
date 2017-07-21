package com.rgand.x_prt.lastfmhits.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rgand.x_prt.lastfmhits.R;
import com.rgand.x_prt.lastfmhits.listener.OnArtistItemClickListener;
import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;
import com.rgand.x_prt.lastfmhits.util.NumberFormatter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.TOP_ARTISTS_RV_PHOTO_SIZE;

/**
 * Created by x_prt on 23.04.2017
 */

public class TopArtistRVAdapter extends RecyclerView.Adapter<TopArtistRVAdapter.ArtistViewHolder> {

    private OnArtistItemClickListener onArtistItemClickListener;

    private List<ArtistModel> artistModelList = new ArrayList<>();

    public TopArtistRVAdapter(OnArtistItemClickListener onArtistItemClickListener) {
        this.onArtistItemClickListener = onArtistItemClickListener;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist_popular, parent, false);
        return new ArtistViewHolder(itemView);
    }

    public void setList(List<ArtistModel> artistModelList) {
        this.artistModelList = artistModelList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        if (!artistModelList.isEmpty()) {
            ArtistModel model = artistModelList.get(position);
            holder.bind(model);
        }
    }

    @Override
    public int getItemCount() {
        return artistModelList.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivArtistPhoto;
        private TextView tvArtistName;
        private TextView tvListeners;

        ArtistViewHolder(View itemView) {
            super(itemView);
            ivArtistPhoto = (ImageView) itemView.findViewById(R.id.iv_artist_photo);
            tvArtistName = (TextView) itemView.findViewById(R.id.tv_artist_name);
            tvListeners = (TextView) itemView.findViewById(R.id.tv_listeners);
        }

        void bind(final ArtistModel model) {
            Picasso.with(itemView.getContext())
                    .load(new File(model.getLargePhotoFilePath()))
                    .resize(TOP_ARTISTS_RV_PHOTO_SIZE, TOP_ARTISTS_RV_PHOTO_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.no_image_placeholder)
                    .error(R.drawable.no_image_placeholder)
                    .into(ivArtistPhoto);

            tvArtistName.setText(model.getName());

            String numOfListeners = NumberFormatter.spacesForBigNumber(model.getListeners())
                    + " "
                    + itemView.getContext().getString(R.string.listeners_suffix_txt);
            tvListeners.setText(numOfListeners);

            //I need mega-sized photo for Artist's Info screen, 'cause large photo is too low quality
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onArtistItemClickListener.onArtistItemClicked(model.getName());
                }
            });
        }
    }
}
