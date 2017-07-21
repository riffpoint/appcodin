package com.rgand.x_prt.lastfmhits.network.listener;

/**
 * Created by x_prt on 21.04.2017
 */

public interface RequestListener<T> {

    void onSuccess(T result);

    void onError(String errorMessage);
}
