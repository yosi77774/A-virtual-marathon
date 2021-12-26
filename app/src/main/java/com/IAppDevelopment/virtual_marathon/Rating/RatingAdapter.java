package com.IAppDevelopment.virtual_marathon.Rating;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.IAppDevelopment.virtual_marathon.Object_classes.data_run;
import com.IAppDevelopment.virtual_marathon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class feeds the display screen for the ranking of race participants,
 * from data it receives.
 */

public class RatingAdapter extends ArrayAdapter<data_run> {

    ArrayList<data_run> objects;
    Context context;
    public RatingAdapter(@NonNull Context context, int resource, @NonNull ArrayList<data_run> objects) {
        super(context, resource, objects);

        this.objects = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  v = inflater.inflate(R.layout.rating_data,null);

        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(objects.get(position).time)%60;
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(objects.get(position).time)%60 ;
        int Hours = (int) TimeUnit.MILLISECONDS.toHours(objects.get(position).time)%24 ;

        TextView tv_date_name = v.findViewById(R.id.tv_name);
        TextView tv_place = v.findViewById(R.id.tv_rating_data);
        TextView tv_time = v.findViewById(R.id.tv_time_data);
        ImageView imageView = v.findViewById(R.id.iv_rating);

        tv_date_name.setText(objects.get(position).data_name);
        tv_place.setText(objects.get(position).place);
        tv_time.setText(String.format("%02d:%02d:%02d",Hours,minutes,seconds));

        Picasso.get().load(objects.get(position).getPic_url()).into(imageView);


        return v;
    }
}
