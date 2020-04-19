package com.kathryn.kathryn.public_profile.adapter;

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

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.MyAdsOnclicklinstener;
import com.kathryn.kathryn.modelsList.myAdsModel;
import com.kathryn.kathryn.utills.SettingsMain;

/**
 * Created by apple on 12/28/17.
 */

public class PublicProfileAdapter extends RecyclerView.Adapter<PublicProfileAdapter.MyViewHolder> {
    private ArrayList<myAdsModel> list;
    private MyAdsOnclicklinstener onItemClickListener;
    private Context mContext;
    private SettingsMain settingsMain;

    private ArrayList<String> temp;

    public PublicProfileAdapter(Context context, ArrayList<myAdsModel> Data) {
        this.list = Data;
        this.mContext = context;
        this.settingsMain = new SettingsMain(mContext);

    }

    @Override
    public PublicProfileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemof_public_profile, parent, false);
        return new PublicProfileAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PublicProfileAdapter.MyViewHolder holder, final int position) {

        final myAdsModel feedItem = list.get(position);

        holder.name.setText(list.get(position).getName());

        if (!TextUtils.isEmpty(feedItem.getImage())) {
            Picasso.with(mContext).load(feedItem.getImage())
                    .resize(270, 270).centerCrop()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }
        holder.priceTV.setText(list.get(position).getPrice());

        holder.linearLayout.setTag(list.get(position).getAdId());

        if (list.get(position).getAdType().equals("myads")) {

            temp = list.get(position).getSpinerValue();
            holder.statusTV.setText(list.get(position).getAdStatusValue());

            if (list.get(position).getAdStatus().equals("expired")) {
                holder.statusTV.setBackgroundColor(Color.parseColor("#d9534f"));
            } else if (list.get(position).getAdStatus().equals("active")) {
                holder.statusTV.setBackgroundColor(Color.parseColor("#4caf50"));
            } else if (list.get(position).getAdStatus().equals("sold")) {
                holder.statusTV.setBackgroundColor(Color.parseColor("#3498db"));
            }
            holder.removeFavBtn.setVisibility(View.GONE);
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

    public void setOnItemClickListener(MyAdsOnclicklinstener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, priceTV, statusTV, removeFavBtn;
        ImageView mainImage;
        RelativeLayout linearLayout;


        boolean spinnerTouched = false;

        MyViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.text_view_name);
            priceTV = v.findViewById(R.id.prices);
            priceTV.setTextColor(Color.parseColor(settingsMain.getMainColor()));
            statusTV = v.findViewById(R.id.textView4);
            mainImage = v.findViewById(R.id.image_view);
            linearLayout = v.findViewById(R.id.linear_layout_card_view);
            removeFavBtn = v.findViewById(R.id.textView17);

        }
    }


}

