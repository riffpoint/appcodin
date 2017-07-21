package com.rgand.x_prt.lastfmhits.util;

import android.content.Context;

import com.rgand.x_prt.lastfmhits.model.artist.ArtistModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.rgand.x_prt.lastfmhits.util.AppConstants.APP_CASH_DIRECTORY;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.APP_CASH_FILE_NAME;
import static com.rgand.x_prt.lastfmhits.util.AppConstants.MEGA_FILE_PREFIX;

/**
 * Created by x_prt on 11.05.2017
 */

public class FileUtils {
    public static void saveFilepath(Context context, ArtistModel artist, final String artistCountry,
                                    String photoUrl, boolean isMegaPhoto) {
        try {

            URL url = new URL(photoUrl);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();

            File cashDirectory = new File(context.getExternalFilesDir(null).getPath()
                    + File.separator
                    + APP_CASH_DIRECTORY
                    + File.separator
                    + artistCountry.replaceAll("\\s", "")
                    + File.separator
                    + artist.getName().replaceAll("\\s", ""));
            cashDirectory.mkdirs();

            String photoFilePath = cashDirectory
                    + File.separator
                    + (isMegaPhoto ? MEGA_FILE_PREFIX : "")
                    + APP_CASH_FILE_NAME;

            File tempFile = new File(photoFilePath);

            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buf = new byte[512];
            while (true) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                fos.write(buf, 0, len);
            }
            in.close();
            fos.flush();
            fos.close();

            if (isMegaPhoto) {
                artist.setMegaPhotoFilePath(photoFilePath);
            } else {
                artist.setLargePhotoFilePath(photoFilePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
