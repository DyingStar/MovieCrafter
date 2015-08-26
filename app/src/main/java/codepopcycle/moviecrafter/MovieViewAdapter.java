package codepopcycle.moviecrafter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import codepopcycle.moviecrafter.tools.MovieListParser;
import codepopcycle.moviecrafter.tools.PropertiesReader;

/**
 * Created by nicolas on 13/08/15.
 */
public class MovieViewAdapter extends BaseAdapter {
    private final static String LOG_TAG = MovieViewAdapter.class.getSimpleName();

    private static final String TMDB_SCHEME = "http";
    private static final String TMDB_LIST_AUTHORITY = "api.themoviedb.org";
    private static final String TMDB_LIST_PATH1 = "3";
    private static final String TMDB_LIST_PATH2 = "discover";
    private static final String TMDB_LIST_PATH3 = "movie";
    private static final String TMDB_LIST_ARG1 = "sort_by";
    private static final String TMDB_LIST_VALUE1 = "popularity.desc";
    private static final String TMDB_LIST_ARG2 = "api_key";
    private static final String TMDB_IMAGE_AUTHORITY = "image.tmdb.org";
    private static final String TMDB_IMAGE_PATH1 = "t";
    private static final String TMDB_IMAGE_PATH2 = "p";
    private static final String TMDB_IMG_SIZE = "w185";

    private final Context mContext;
    private String[] movieList;

    public MovieViewAdapter(Context mContext) {
        this.mContext = mContext;
        this.movieList = new String[0];

        this.refreshMovies();

        Log.d(LOG_TAG, "Created MovieViewAdapter");
    }

    private void refreshMovies() {
        RetrieveMovieTask movieTask = new RetrieveMovieTask();
        movieTask.execute();
    }

    @Override
    public int getCount() {
        return movieList.length;
    }

    @Override
    public Object getItem(int position) {
        return movieList[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(((Activity)mContext).findViewById(R.id.activity_view_moviegrid).getLayoutParams());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 450));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri imageUri = this.createImageUri(movieList[position]);

        Log.d(LOG_TAG, "Going to town with Picasso and Uri" + imageUri);
        Picasso.with(mContext).load(imageUri).into(imageView);
        Log.d(LOG_TAG, "Picasso did its thing");

        return imageView;
    }

    private Uri createImageUri(String movieId) {
        String posterImage = movieId.substring(1);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(TMDB_SCHEME)
                .authority(TMDB_IMAGE_AUTHORITY)
                .appendPath(TMDB_IMAGE_PATH1)
                .appendPath(TMDB_IMAGE_PATH2)
                .appendPath(TMDB_IMG_SIZE)
                .appendPath(posterImage);
        return builder.build();
    }

    private class RetrieveMovieTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            return this.retrieveMovieList();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            movieList = strings;
            notifyDataSetChanged();
        }

        private Uri createListUri() {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(TMDB_SCHEME).
                    authority(TMDB_LIST_AUTHORITY).
                    appendPath(TMDB_LIST_PATH1).
                    appendPath(TMDB_LIST_PATH2).
                    appendPath(TMDB_LIST_PATH3).
                    appendQueryParameter(TMDB_LIST_ARG1, TMDB_LIST_VALUE1).
                    appendQueryParameter(TMDB_LIST_ARG2, PropertiesReader.readProperty(mContext, "imdbkey"));
            return builder.build();
        }

        public String[] retrieveMovieList() {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonString = null;

            try {
                URL completeUri = (URL) new URL(createListUri().toString());

                Log.v(LOG_TAG, "URI was built: " + completeUri.toString());

                // Create the request to TMDB, and open the connection
                urlConnection = (HttpURLConnection) completeUri.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonString = buffer.toString();

                Log.v(LOG_TAG, "Movie JSON String: " + movieJsonString);

                String[] movieData = null;
                try {
                    movieData = MovieListParser.getMovieDataFromJson(movieJsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return movieData;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
    }
}
