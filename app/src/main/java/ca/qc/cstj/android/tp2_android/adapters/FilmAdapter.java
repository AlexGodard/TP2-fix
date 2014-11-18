package ca.qc.cstj.android.tp2_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import ca.qc.cstj.android.tp2_android.R;
import ca.qc.cstj.android.tp2_android.models.Film;

/**
 * Created by 1247308 on 2014-10-30.
 */
public class FilmAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private JsonArray mJsonArray;


    public FilmAdapter(Context context, LayoutInflater inflater, JsonArray jsonArray) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return mJsonArray.size();
    }

    @Override
    public JsonObject getItem(int position) {
        return mJsonArray.get(position).getAsJsonObject();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FilmViewHolder filmViewHolder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.row_film,null);
            filmViewHolder = new FilmViewHolder();
            filmViewHolder.txtTitre = (TextView)convertView.findViewById(R.id.txtTitre);

            convertView.setTag(filmViewHolder);
        } else {
            filmViewHolder = (FilmViewHolder)convertView.getTag();
        }

        JsonObject film = getItem(position);

        filmViewHolder.txtTitre.setText(film.getAsJsonPrimitive("titre").getAsString());


        return convertView;

    }

    private static class FilmViewHolder {
        public TextView txtTitre;
    }
}
