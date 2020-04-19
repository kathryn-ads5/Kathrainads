package com.kathryn.kathryn.home.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.wonshinhyo.dragrecyclerview.DragAdapter;
import com.wonshinhyo.dragrecyclerview.DragHolder;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;

import java.util.ArrayList;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.MyAdsOnclicklinstener;
import com.kathryn.kathryn.modelsList.myAdsModel;

public class ItemEditImageAdapter extends DragAdapter {
    private ArrayList<myAdsModel> list;
    private MyAdsOnclicklinstener onItemClickListener;
    private Context mContext;

    public ItemEditImageAdapter(Context context, ArrayList<myAdsModel> Data) {
        super(context, Data);
        this.list = Data;
        this.mContext = context;
    }


    @Override
    public DragRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.itemof_edit_image, parent, false), viewType);
    }


    @Override
    public void onBindViewHolder(final DragRecyclerView.ViewHolder hol, @SuppressLint("RecyclerView") final int position) {
        MyViewHolder holder = (MyViewHolder) hol;

        final myAdsModel feedItem = list.get(position);

        if (!TextUtils.isEmpty(feedItem.getImage())) {
            Picasso.with(mContext).load(feedItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }
        holder.delAd.setTag(list.get(position).getAdId());

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.delViewOnClick(v, position);
            }
        };


        holder.delAd.setOnClickListener(listener2);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getAllTags() {
        String s = "";
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final myAdsModel feedItem = list.get(i);

                s = s.concat(feedItem.getAdId() + ",");
            }
        }
        return s;
    }

    public void setOnItemClickListener(MyAdsOnclicklinstener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    class MyViewHolder extends DragHolder {

        ImageView mainImage, delAd;
        RelativeLayout relativeLayout;

        MyViewHolder(View v, int viewtype) {
            super(v);

            relativeLayout = v.findViewById(R.id.linear_layout_card_view);
            delAd = v.findViewById(R.id.delAdd);
            mainImage = v.findViewById(R.id.image_view);
        }

    }
}
