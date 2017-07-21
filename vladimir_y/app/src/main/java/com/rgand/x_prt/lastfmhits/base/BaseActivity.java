package com.rgand.x_prt.lastfmhits.base;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rgand.x_prt.lastfmhits.R;
import com.rgand.x_prt.lastfmhits.dialog.SpinnerDialog;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by x_prt on 24.04.2017
 */

public class BaseActivity extends AppCompatActivity {

    private AlertDialog networkDialog;
    private SpinnerDialog spinnerDialog;
    private Snackbar snackbar;
    private DialogInterface.OnClickListener onDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_NEGATIVE:
                    finishAffinity();
                    break;
                case BUTTON_POSITIVE:
                    networkDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void checkInternetConnection(Context context) {
        String message = getString(R.string.offline_mode_txt);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            showSnackMessage(message);
        } else {
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
    }

    private void showInternetConnectionDialog() {
        if (networkDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            networkDialog = builder.create();
            networkDialog.setMessage(getString(R.string.no_internet_message_txt));
            networkDialog.setButton(BUTTON_NEGATIVE,
                    getString(R.string.exit_txt), onDialogClickListener);
            networkDialog.setButton(BUTTON_POSITIVE,
                    getString(R.string.retry_txt), onDialogClickListener);
        }
        networkDialog.show();
    }

    public void showProgressBar() {
        spinnerDialog = SpinnerDialog.newInstance();
        spinnerDialog.show(getSupportFragmentManager(), this.getClass().getSimpleName());
    }

    public void hideProgressBar() {
        if (spinnerDialog != null && spinnerDialog.getShowsDialog()) {
            spinnerDialog.dismiss();
        }
    }


    public void showSnackMessage(String message) {
        if (getWindow().getDecorView() != null) {
            snackbar = Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.ok_txt), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.site_red));
            ViewGroup group = (ViewGroup) snackbar.getView();
            group.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snack_bar_color_background));
            TextView textView = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            snackbar.show();
        }
    }
}
