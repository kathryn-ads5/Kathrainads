package com.kathryn.kathryn.userAndSellers.adapter;

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
import com.kathryn.kathryn.helper.BlockUserClickListener;
import com.kathryn.kathryn.modelsList.blockUserModel;
import com.kathryn.kathryn.utills.SettingsMain;

public class ItemBlockUserAdapter extends RecyclerView.Adapter<ItemBlockUserAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<blockUserModel> feedItemList;
    private Context mContext;
    private BlockUserClickListener blockUserClickListener;

    public ItemBlockUserAdapter(Context context, List<blockUserModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        settingsMain = new SettingsMain(context);

    }

    @Override
    public ItemBlockUserAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blocked_user, null);
        return new ItemBlockUserAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemBlockUserAdapter.CustomViewHolder customViewHolder, final int i) {
        final blockUserModel feedItem = feedItemList.get(i);

        customViewHolder.tv_user_name.setText(feedItemList.get(i).getName());
        if (!TextUtils.isEmpty(feedItem.getLocaiton())) {
            customViewHolder.tv_user_address.setText(feedItemList.get(i).getLocaiton());
        } else
            customViewHolder.tv_user_address.setVisibility(View.GONE);

        customViewHolder.tv_unblock_user.setText(feedItemList.get(i).getText());


        if (!TextUtils.isEmpty(feedItem.getImage())) {
            Picasso.with(mContext).load(feedItem.getImage())
                    .resize(270, 270)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.profileImage);
        }
        View.OnClickListener listener = v -> blockUserClickListener.onItemClick(feedItem, i);

        customViewHolder.tv_unblock_user.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public BlockUserClickListener getOnItemClickListener() {
        return blockUserClickListener;
    }

    public void setOnItemClickListener(BlockUserClickListener onItemClickListener) {
        this.blockUserClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView tv_user_name, tv_user_address, tv_unblock_user;

        CustomViewHolder(View view) {
            super(view);


            this.profileImage = view.findViewById(R.id.profileImage);

            this.tv_user_name = view.findViewById(R.id.tv_user_name);
            this.tv_user_address = view.findViewById(R.id.tv_user_address);
            this.tv_unblock_user = view.findViewById(R.id.tv_unblock_user);
        }
    }
}
