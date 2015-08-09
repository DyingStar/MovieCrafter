package codepopcycle.moviecrafter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviePosterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoviePosterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviePosterFragment extends Fragment {
    private static final String ARG_MOVIE_ID = "movieId";

    private static final String TMDB_SCHEME = "http";
    private static final String TMDB_AUTHORITY = "image.tmdb.org";
    private static final String TMDB_PATH1 = "t";
    private static final String TMDB_PATH2 = "p";
    private static final String TMDB_IMG_SIZE = "w185";

    // Movie ID, of the format '/<movieID>.jpg'
    private String movieId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId The ID of the movie to be displayed in this fragment
     * @return A new instance of fragment MoviePosterFragment.
     */
    public static MoviePosterFragment newInstance(String movieId) {
        MoviePosterFragment fragment = new MoviePosterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    public MoviePosterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getString(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view
        View rootView = inflater.inflate(R.layout.fragment_movieposter, container, false);

        // Get the ImageView for displaying the poster within this fragment
        ImageView posterView = (ImageView) this.getActivity().findViewById(R.id.fragment_view_poster);

        // Let Picasso load the image into the poster view for optimal efficiency
        Picasso.with(this.getActivity()).load(this.createUri()).into(posterView);

        return rootView;
    }

    private Uri createUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(TMDB_SCHEME)
                .authority(TMDB_AUTHORITY)
                .appendPath(TMDB_PATH1)
                .appendPath(TMDB_PATH2)
                .appendPath(TMDB_IMG_SIZE)
                .appendPath(movieId);
        return builder.build();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
