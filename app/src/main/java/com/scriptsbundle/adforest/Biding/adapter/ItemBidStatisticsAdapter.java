package com.kathryn.kathryn.Biding.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.victorminerva.widget.edittext.AutofitEdittext;

import java.util.List;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.modelsList.bidStatisticsModel;

public class ItemBidStatisticsAdapter extends RecyclerView.Adapter<ItemBidStatisticsAdapter.CustomViewHolder> {

    private List<bidStatisticsModel> feedItemList;
    private Context mContext;

    public ItemBidStatisticsAdapter(Context context, List<bidStatisticsModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;

    }

    @Override
    public ItemBidStatisticsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemof_biding_statistics, null);
        return new ItemBidStatisticsAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemBidStatisticsAdapter.CustomViewHolder customViewHolder, int i) {
        final bidStatisticsModel feedItem = feedItemList.get(i);

//        customViewHolder.htmlTextView.setHtml("<font color=\"#242424\">" + feedItem.getUserName() +
//                "<font color=\"#878787\">" +" "+ feedItem.getPostedText());
        customViewHolder.tv_user_name.setText(feedItem.getUserName());
        customViewHolder.tv_offer_by.setText(feedItem.getPostedText());
        customViewHolder.tv_date.setText(feedItem.getDate());
        customViewHolder.tv_price.setText(feedItem.getPrice());
        customViewHolder.tv_total_count.setText(feedItem.getCount());

        if (!TextUtils.isEmpty(feedItem.getProfileImage())) {
            Picasso.with(mContext).load(feedItem.getProfileImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_price, tv_date, tv_user_name, tv_total_count;
        AutofitEdittext tv_offer_by;

        CustomViewHolder(View view) {
            super(view);

            this.imageView = view.findViewById(R.id.image_view);
            this.tv_price = view.findViewById(R.id.tv_price);
            this.tv_date = view.findViewById(R.id.tv_date);
            this.tv_user_name = view.findViewById(R.id.tv_user_name);
            this.tv_offer_by = view.findViewById(R.id.tv_offer_by);
            this.tv_offer_by = view.findViewById(R.id.tv_offer_by);
            this.tv_total_count = view.findViewById(R.id.tv_total_count);

        }
    }
}
