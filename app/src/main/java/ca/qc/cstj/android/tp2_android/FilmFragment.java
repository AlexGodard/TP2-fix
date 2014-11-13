package ca.qc.cstj.android.tp2_android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import ca.qc.cstj.android.tp2_android.adapters.FilmAdapter;
import ca.qc.cstj.android.tp2_android.services.ServicesURI;

/**
 * Created by 1247308 on 2014-10-30.
 */
public class FilmFragment extends Fragment{


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private ListView lstFilm;
        private ProgressDialog progressDialog;
        private FilmAdapter filmAdapter;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FilmFragment newInstance(int sectionNumber) {
            FilmFragment fragment = new FilmFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FilmFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_film, container, false);

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();

            lstFilm = (ListView) getActivity().findViewById(R.id.list_films);

            loadFilms();

            lstFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String href = filmAdapter.getItem(position).getAsJsonPrimitive("href").getAsString();

                    FragmentTransaction transaction =  getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container,DetailFilmFragment.newInstance(href))
                            .addToBackStack("");
                    transaction.commit();

                }
            });

        }

        private void loadFilms()
        {
            progressDialog = ProgressDialog.show(getActivity(),"Films","En chargement...",true,false);

            Ion.with(getActivity())
                    .load(ServicesURI.FILMS_SERVICE_URI)
                    .asJsonArray()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonArray>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonArray> response) {

                            filmAdapter = new FilmAdapter(getActivity(),
                                    getActivity().getLayoutInflater(),response.getResult());
                            lstFilm.setAdapter(filmAdapter);

                            progressDialog.dismiss();
                        }
                    });


        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }



}
