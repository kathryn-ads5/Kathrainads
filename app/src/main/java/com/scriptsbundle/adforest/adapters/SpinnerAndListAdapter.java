package com.kathryn.kathryn.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.modelsList.subcatDiloglist;

public class SpinnerAndListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<subcatDiloglist> itemList;
    private boolean active = false;

    public SpinnerAndListAdapter(Activity activity, ArrayList<subcatDiloglist> list) {
        itemList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public SpinnerAndListAdapter(Activity activity, ArrayList<subcatDiloglist> list, boolean s) {
        itemList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        active = s;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final subcatDiloglist item = itemList.get(position);
        View vi = convertView;
        if (convertView == null) {
            if (active)
                vi = inflater.inflate(R.layout.spinner_item_medium, null);
            else
                vi = inflater.inflate(R.layout.spinner_itemlarge, null);
        }


        vi.setTag(item);
        TextView name = vi.findViewById(R.id.text_view_name);
        name.setText(item.getName());

        return vi;
    }


}