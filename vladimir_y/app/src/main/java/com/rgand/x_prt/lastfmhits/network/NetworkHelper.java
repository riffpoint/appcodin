package com.rgand.x_prt.lastfmhits.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.API_BASE_URL;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.API_KEY;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.API_REQUEST_FORMAT;

/**
 * Created by x_prt on 21.04.2017
 */

public class NetworkHelper {

    private static NetworkHelper ourInstance = new NetworkHelper();
    private LastFmApi appService;

    private NetworkHelper() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();
                        HttpUrl url = originalHttpUrl.newBuilder()
                                .addQueryParameter("api_key", API_KEY)
                                .addQueryParameter("format", API_REQUEST_FORMAT)
                                .build();
                        Request.Builder requestBuilder = original.newBuilder().url(url);
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        GsonConverterFactory factory = GsonConverterFactory.create(gson);
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(factory)
                .build();
        appService = retrofit.create(LastFmApi.class);
    }

    public static NetworkHelper getInstance() {
        return ourInstance;
    }

    public LastFmApi getAppService() {
        return appService;
    }
}
