package codepopcycle.moviecrafter;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nicolas on 09/08/15.
 */
public class PropertiesReader {

    public static final String LOG_TAG = PropertiesReader.class.getSimpleName();

    public static String readProperty(Context context, String key) {
        Properties props = new Properties();
        try {
            // Get input stream from properties file
            InputStream propertiesStream = context.getAssets().open(context.getResources().getString(R.string.properties_local));

            // If input stream was successfully created, read it
            props.load(propertiesStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return props.getProperty(key);
    }
}
