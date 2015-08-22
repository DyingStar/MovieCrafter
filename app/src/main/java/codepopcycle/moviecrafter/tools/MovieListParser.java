package codepopcycle.moviecrafter.tools;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nicolas on 13/08/15.
 */
public class MovieListParser {
    private final static String LOG_TAG = MovieListParser.class.getSimpleName();

    public static String[] getMovieDataFromJson(String jsonMovieList) throws JSONException {
        JSONObject jsonMovie = new JSONObject(jsonMovieList);
        JSONArray movieArray = jsonMovie.getJSONArray("results");

        Log.i(LOG_TAG, "Retrieved " + movieArray.length() + " results from TMDB");

        String[] resultList = new String[movieArray.length()];

        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            resultList[i] = movieObject.getString("poster_path");
        }

        return resultList;
    }
}
