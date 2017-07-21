package com.rgand.x_prt.lastfmhits.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.rgand.x_prt.lastfmhits.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by x_prt on 24.04.2017
 */

public class SpinnerDialog extends DialogFragment {

    private Dialog dialog;

    public static SpinnerDialog newInstance() {
        return new SpinnerDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dialog = getDialog();
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_spinner, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dialog != null) {

            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(MATCH_PARENT, MATCH_PARENT);
            }
        }
    }
}
