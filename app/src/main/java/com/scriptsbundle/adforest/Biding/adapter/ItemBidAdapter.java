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

import java.util.List;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.modelsList.bidModel;

public class ItemBidAdapter extends RecyclerView.Adapter<ItemBidAdapter.CustomViewHolder> {

    private List<bidModel> feedItemList;
    private Context mContext;

    public ItemBidAdapter(Context context, List<bidModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;

    }

    @Override
    public ItemBidAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bid_layout, null);
        return new ItemBidAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemBidAdapter.CustomViewHolder customViewHolder, int i) {
        final bidModel feedItem = feedItemList.get(i);

        customViewHolder.name.setText(feedItem.getBidUserNmae());
        customViewHolder.price.setText(feedItem.getBidPrice());
        customViewHolder.date.setText(feedItem.getBidDate());
        customViewHolder.message.setText(feedItem.getBidMessage());
        if (feedItem.getBidPhoneNumber().isEmpty())
            customViewHolder.phone.setVisibility(View.GONE);
        else {
            customViewHolder.phone.setText(feedItem.getBidPhoneNumber());
            customViewHolder.phone.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(feedItem.getBidImage())) {
            Picasso.with(mContext).load(feedItem.getBidImage())
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
        TextView name, date, message, price, phone;

        CustomViewHolder(View view) {
            super(view);

            this.imageView = view.findViewById(R.id.profile_image);

            this.name = view.findViewById(R.id.text_viewName);
            this.price = view.findViewById(R.id.verified);
            this.date = view.findViewById(R.id.date);
            this.message = view.findViewById(R.id.message);
            this.phone = view.findViewById(R.id.phone);
        }
    }
}
