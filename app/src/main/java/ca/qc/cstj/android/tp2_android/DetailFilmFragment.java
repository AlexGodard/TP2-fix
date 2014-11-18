package ca.qc.cstj.android.tp2_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ca.qc.cstj.android.tp2_android.adapters.CinemaAdapter;
import ca.qc.cstj.android.tp2_android.adapters.CommentaireAdapter;
import ca.qc.cstj.android.tp2_android.adapters.HoraireAdapter;
import ca.qc.cstj.android.tp2_android.helpers.DateParser;
import ca.qc.cstj.android.tp2_android.models.Commentaire;
import ca.qc.cstj.android.tp2_android.models.Film;
import ca.qc.cstj.android.tp2_android.models.Horaire;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFilmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFilmFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DetailFilmFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HREF = "href";

    // TODO: Rename and change types of parameters
    private String href;
    private TextView tvTitre;
    private TextView tvPays;
    private TextView tvClasse;
    private TextView tvGenre;
    private TextView tvRealisateur;
    private TextView tvDuree;
    private ImageView ivImage;

    private ListView lstCommentaire;
    private CommentaireAdapter commentaireAdapter;

    private EditText etCommentaire;
    private EditText etPseudo;
    private EditText etNote;

    private Button btnAjouter;

    private Film film;

    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    /*/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param href href de l'employé a afficher
     * @return A new instance of fragment DetailFilmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFilmFragment newInstance(String href) {
        DetailFilmFragment fragment = new DetailFilmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HREF, href);
        fragment.setArguments(args);
        return fragment;
    }
    public DetailFilmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            href = getArguments().getString(ARG_HREF);
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_film, container, false);

        tvTitre = (TextView) view.findViewById(R.id.tvTitre);
        tvPays = (TextView) view.findViewById(R.id.tvPays);
        tvGenre = (TextView) view.findViewById(R.id.tvGenre);
        tvClasse = (TextView) view.findViewById(R.id.tvClasse);
        tvRealisateur = (TextView)view.findViewById(R.id.tvRealisateur);
        tvDuree = (TextView)view.findViewById(R.id.tvDuree);
        ivImage = (ImageView)view.findViewById(R.id.ivImage);
        etCommentaire = (EditText) view.findViewById(R.id.etCommentaire);
        etNote = (EditText)view.findViewById(R.id.etNote);
        etPseudo = (EditText)view.findViewById(R.id.etPseudo);
        btnAjouter = (Button)view.findViewById(R.id.btnAjouter);

        btnAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajouterCommentaire();
            }
        });

        return view;
    }

    private void ajouterCommentaire() {

        JsonObject body = new JsonObject();
        // TODO: ID
        if(!etCommentaire.getText().toString().isEmpty() && !etNote.getText().toString().isEmpty() && !etPseudo.getText().toString().isEmpty()) {
            body.addProperty("idFilm",href.substring(href.length() - 1));
            body.addProperty("texte",etCommentaire.getText().toString());
            body.addProperty("note",etNote.getText().toString());
            body.addProperty("auteur",etPseudo.getText().toString());

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sss'Z'");
            String nowAsISO = df.format(new Date());

            body.addProperty("dateHeure", nowAsISO);

            Ion.with(getActivity())
                    .load("POST",href + "/commentaires")
                    .addHeader("Content-Type", "application/json")
                    .setJsonObjectBody(body)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject jsonObject) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Commentaire ajouté!", Toast.LENGTH_LONG).show();
                            etCommentaire.setText("");
                            etNote.setText("");
                            etPseudo.setText("");
                        }
                    });
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Erreur : Vous devez entrer du texte, une note, et un pseudo!", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        lstCommentaire = (ListView) getActivity().findViewById(R.id.list_commentaires);

        progressDialog = ProgressDialog.show(getActivity(), "", "En chargement...", true, false);
        Ion.with(getActivity())
                .load(href)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject jsonObject) {

                        film = new Film(jsonObject);

                        Ion.with(ivImage)
                                .load(film.getImageUrl());

                        tvTitre.setText(film.getTitre());
                        tvPays.setText(film.getPays());
                        tvGenre.setText(film.getGenre());
                        tvDuree.setText("Durée : " + film.getDuree().toString() + " minutes");
                        tvClasse.setText(film.getClasse());
                        tvRealisateur.setText("Réalisateur : " + film.getRealisateur());

                        Ion.with(getActivity())
                                .load(href + "/commentaires")
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject jsonObject) {

                                        ArrayList<Commentaire> listeCommentaires = new ArrayList<Commentaire>();

                                        if (jsonObject.has("commentaires")) {
                                            JsonArray commentaires = jsonObject.getAsJsonArray("commentaires");
                                            // On veut juste en afficher 2, on compte le nombre de fois qu'on instancie un horaire pour un cinéma

                                            for (JsonElement commentaire : commentaires) {
                                                listeCommentaires.add(new Commentaire(commentaire.getAsJsonObject()));
                                            }

                                            commentaireAdapter = new CommentaireAdapter(getActivity(), getActivity().getLayoutInflater(), listeCommentaires);
                                            lstCommentaire.setAdapter(commentaireAdapter);
                                        }
                                    }
                                });

                        progressDialog.dismiss();

                    }

        });
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
