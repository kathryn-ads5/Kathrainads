package com.kathryn.kathryn.home.adapter;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import com.kathryn.kathryn.R;

/**
 * Created by Glixen Technologies on 22/12/2017.
 */

public class Adforest_SpinnerAdapter extends ArrayAdapter<String> {
    private boolean disableFirstIndex = false;
    private boolean shouldTextColorBeWhite;

    public Adforest_SpinnerAdapter(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        }
    public Adforest_SpinnerAdapter(@NonNull Context context, int resource, List<String> objects, boolean disableFirstIndex) {
        super(context, resource, objects);
        this.disableFirstIndex = disableFirstIndex;
    }


    public void setTextColorWhire(Boolean shouldTextColorBeWhite){this.shouldTextColorBeWhite = shouldTextColorBeWhite;}

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        if(shouldTextColorBeWhite)
            view.setTextColor(getContext().getResources().getColor(R.color.white));
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        return view;
    }

    @Override
    public boolean isEnabled(int position) {

    if(disableFirstIndex) {
        if (position == 0)
            return false;
        else return true;
    }
    else
        return true;
           }
}
