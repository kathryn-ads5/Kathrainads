package com.kathryn.kathryn.userAndSellers.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.modelsList.sellersModel;
import com.kathryn.kathryn.public_profile.social_icons;
import com.kathryn.kathryn.utills.SettingsMain;

public class ItemSellersListAdapter extends RecyclerView.Adapter<ItemSellersListAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<sellersModel> feedItemList;
    private Context mContext;
    private sellersListClickListener blockUserClickListener;

    public ItemSellersListAdapter(Context context, List<sellersModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        settingsMain = new SettingsMain(context);

    }

    @Override
    public ItemSellersListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sellers_list, null);
        return new ItemSellersListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemSellersListAdapter.CustomViewHolder customViewHolder, final int i) {
        final sellersModel feedItem = feedItemList.get(i);

        customViewHolder.tv_author_name.setText(feedItem.getAuthour_name());
        customViewHolder.tv_author_rating.setRating(Float.parseFloat(feedItem.getAuthor_rating()));
        if (!feedItem.getAuthor_location().isEmpty()) {
            customViewHolder.tv_author_location.setVisibility(View.VISIBLE);
            customViewHolder.tv_author_location.setText(feedItem.getAuthor_location());
        }
        try {
            if (feedItem.getAuthor_social().getBoolean("is_show_social")) {
                customViewHolder.sellersSoicalIcons.setVisibility(View.VISIBLE);
                social_icons.adforest_setViewsForCustom(feedItem.getAuthor_social(), customViewHolder.sellersSoicalIcons, mContext);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (!TextUtils.isEmpty(feedItem.getAuthor_img())) {
            Picasso.with(mContext).load(feedItem.getAuthor_img())
                    .resize(270, 270)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.profileImage);
        }
        View.OnClickListener listener = v -> blockUserClickListener.onClick(feedItem);

        customViewHolder.card_view.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public sellersListClickListener getOnItemClickListener() {
        return blockUserClickListener;
    }

    public void setOnItemClickListener(sellersListClickListener onItemClickListener) {
        this.blockUserClickListener = onItemClickListener;
    }

    public interface sellersListClickListener {
        public void onClick(sellersModel sellersModel);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView tv_author_name, tv_author_location;
        LinearLayout sellersSoicalIcons;
        RatingBar tv_author_rating;
        CardView card_view;

        CustomViewHolder(View view) {
            super(view);

            this.profileImage = view.findViewById(R.id.profileImage);
            this.tv_author_name = view.findViewById(R.id.tv_author_name);
            this.sellersSoicalIcons = view.findViewById(R.id.sellersSoicalIcons);
            this.tv_author_rating = view.findViewById(R.id.tv_author_rating);
            this.tv_author_location = view.findViewById(R.id.tv_author_location);
            this.card_view = view.findViewById(R.id.card_view);
        }
    }
}

