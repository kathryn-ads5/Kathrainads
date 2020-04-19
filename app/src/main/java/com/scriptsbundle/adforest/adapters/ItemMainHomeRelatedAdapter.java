package com.kathryn.kathryn.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.OnItemClickListener2;
import com.kathryn.kathryn.modelsList.catSubCatlistModel;
import com.kathryn.kathryn.utills.AdsTimerConvert;
import com.kathryn.kathryn.utills.SettingsMain;


public class ItemMainHomeRelatedAdapter extends RecyclerView.Adapter<ItemMainHomeRelatedAdapter.MyViewHolder> {

    SettingsMain settingsMain;
    Context context;
    private ArrayList<catSubCatlistModel> list;
    private OnItemClickListener2 onItemClickListener;

    public ItemMainHomeRelatedAdapter(Context context, ArrayList<catSubCatlistModel> Data) {
        this.list = Data;
        settingsMain = new SettingsMain(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_home_related, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final catSubCatlistModel feedItem = list.get(position);

        holder.titleTextView.setText(list.get(position).getCardName());
        holder.dateTV.setText(list.get(position).getDate());
        holder.priceTV.setText(list.get(position).getPrice());
        holder.locationTV.setText(list.get(position).getLocation());
        if (list.get(position).getFeaturetype()) {
            holder.featureText.setVisibility(View.VISIBLE);
            holder.featureText.setText(list.get(position).getAddTypeFeature());
            holder.featureText.setBackgroundColor(Color.parseColor("#E52D27"));
        }
        if (list.get(position).isIs_show_countDown()) {
            holder.cv_countdownView.setVisibility(View.VISIBLE);
            holder.cv_countdownView.start(AdsTimerConvert.adforest_bidTimer(list.get(position).getTimer_array()));
        }
        else{
            holder.cv_countdownView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(feedItem.getImageResourceId())) {
            Picasso.with(context).load(feedItem.getImageResourceId())
                    .resize(250, 250).centerCrop()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(feedItem);
            }
        };

        holder.linearLayout.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener2 onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTV, priceTV, locationTV, featureText;
        ImageView mainImage;
        RelativeLayout linearLayout;
        CountdownView cv_countdownView;

        MyViewHolder(View v) {
            super(v);

            titleTextView = v.findViewById(R.id.text_view_name);
            dateTV = v.findViewById(R.id.date);
            priceTV = v.findViewById(R.id.prices);
            locationTV = v.findViewById(R.id.location);
            priceTV.setTextColor(Color.parseColor(settingsMain.getMainColor()));
            mainImage = v.findViewById(R.id.image_view);
            cv_countdownView = v.findViewById(R.id.cv_countdownView);

            linearLayout = v.findViewById(R.id.linear_layout_card_view);
            featureText = v.findViewById(R.id.textView4);
        }
    }
}
