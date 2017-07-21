package com.rgand.x_prt.lastfmhits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.rgand.x_prt.lastfmhits.R;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.SPLASH_ANIMATION_DELAY;

public class SplashActivity extends AppCompatActivity {

    /**
     * there is no longtime NET-requests or loading of data therefore splash is created only
     * for presentation of application's creators
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.slide_up_in_animation, R.anim.fade_in_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, PopularArtistActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_ANIMATION_DELAY);
    }
}
