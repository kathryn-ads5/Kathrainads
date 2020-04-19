package com.kathryn.kathryn.home.helper;

import android.widget.Filter;

import java.util.ArrayList;

import com.kathryn.kathryn.home.adapter.ItemMainAllLocationAds;
import com.kathryn.kathryn.home.adapter.ItemMainAllLocationPoPUpHome;
import com.kathryn.kathryn.modelsList.homeCatListModel;

public class CustomLocationFilterPopUP extends Filter {

    private ItemMainAllLocationPoPUpHome adapter;
    private ArrayList<homeCatListModel> filterList;

    public CustomLocationFilterPopUP(ArrayList<homeCatListModel> filterList, ItemMainAllLocationPoPUpHome adapter) {
        this.adapter = adapter;
        this.filterList = filterList;

    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if (constraint != null && constraint.length() > 0) {
            //CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<homeCatListModel> filteredPlayers = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //CHECK
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)) {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }

            results.count = filteredPlayers.size();
            results.values = filteredPlayers;
        } else {
            results.count = filterList.size();
            results.values = filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.list = (ArrayList<homeCatListModel>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
