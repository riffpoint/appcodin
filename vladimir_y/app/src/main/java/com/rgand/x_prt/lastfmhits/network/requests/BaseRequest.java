package com.rgand.x_prt.lastfmhits.network.requests;


import com.rgand.x_prt.lastfmhits.network.listener.RequestListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by x_prt on 21.04.2017
 */

public abstract class BaseRequest<T> {

    private String errorMessage = "Unknown server error";

    private RequestListener<T> listener;
    private Callback<T> callback = new Callback<T>() {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (listener == null) {
                return;
            }
            if (response.isSuccessful()) {
                listener.onSuccess(response.body());
            } else {
                listener.onError(errorMessage);
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            errorMessage = t.getMessage();
            if (listener != null) {
                listener.onError(errorMessage);
            }
        }
    };

    public void execute() {
        getCall().enqueue(callback);
    }

    public void setListener(RequestListener<T> listener) {
        this.listener = listener;
    }

    public abstract Call<T> getCall();
}
