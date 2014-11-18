package ca.qc.cstj.android.tp2_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.joda.time.DateTime;

import java.util.ArrayList;
import ca.qc.cstj.android.tp2_android.helpers.DateParser;
import ca.qc.cstj.android.tp2_android.R;
import ca.qc.cstj.android.tp2_android.models.Horaire;

/**
 * Created by 1247308 on 2014-11-05.
 */
public class HoraireAdapter extends ArrayAdapter<Horaire> {
    private ArrayList<Horaire> horaires;
    private LayoutInflater layoutInflater;


    public HoraireAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Horaire> listeHoraires) {
        super(context, R.layout.row_horaire,listeHoraires);
        this.horaires = listeHoraires;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final HoraireViewHolder horaireViewHolder;
        final View myView = layoutInflater.inflate(R.layout.row_horaire,null,true);

        if(convertView == null) {
            horaireViewHolder = new HoraireViewHolder();
            horaireViewHolder.txtTitre = (TextView)myView.findViewById(R.id.txtTitre);
            horaireViewHolder.txtHoraire1 = (TextView)myView.findViewById(R.id.txtHoraire1);
            horaireViewHolder.txtHoraire2 = (TextView)myView.findViewById(R.id.txtHoraire2);

            myView.setTag(horaireViewHolder);
        } else {
            horaireViewHolder = (HoraireViewHolder)myView.getTag();
        }

        final Horaire horaire = getItem(position);

        Ion.with(getContext())
                .load(horaire.getHref() + "/films/" + horaire.getIdFilm() + "/horaires?days=2")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray jsonArray) {

                        HoraireViewHolder horaireViewHolder = (HoraireViewHolder)myView.getTag();
                        int i = 0;
                        for (JsonElement eHoraire : jsonArray) {
                            JsonObject objHoraire = eHoraire.getAsJsonObject();

                            if (i == 0) {
                                DateTime dtDateHeure = DateParser.ParseIso(objHoraire.getAsJsonPrimitive("dateHeure").getAsString());
                                horaire.setDate1(DateParser.ParseToDate(dtDateHeure));
                                horaire.setHeure1(DateParser.ParseToTime(dtDateHeure));
                            }

                            if (i == 1) {
                                DateTime dtDateHeure = DateParser.ParseIso(objHoraire.getAsJsonPrimitive("dateHeure").getAsString());
                                horaire.setDate2(DateParser.ParseToDate(dtDateHeure));
                                horaire.setHeure2(DateParser.ParseToTime(dtDateHeure));
                            }

                            i++;
                        }

                        horaireViewHolder.txtTitre.setText(horaire.getTitre());
                        //S'il n'y a pas d'horaire, on ne l'affiche pas
                        if(horaire.getDate1() == null) {
                            horaireViewHolder.txtHoraire1.setText("");
                        } else {
                            horaireViewHolder.txtHoraire1.setText(horaire.getDate1() + ", " + horaire.getHeure1());
                        }

                        if(horaire.getDate2() == null) {
                            horaireViewHolder.txtHoraire2.setText("");
                        } else {
                            horaireViewHolder.txtHoraire2.setText(horaire.getDate2() + ", " + horaire.getHeure2());
                        }
                    }

                });

        return myView;

    }

    private void updateView(HoraireViewHolder horaireViewHolder, Horaire horaire){


    }

    private class HoraireViewHolder {
        public TextView txtTitre;
        public TextView txtHoraire1;
        public TextView txtHoraire2;
    }
}
