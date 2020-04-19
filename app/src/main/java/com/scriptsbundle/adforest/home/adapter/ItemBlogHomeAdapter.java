package com.kathryn.kathryn.home.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.BlogItemOnclicklinstener;
import com.kathryn.kathryn.modelsList.blogModel;
import com.kathryn.kathryn.utills.SettingsMain;

public class ItemBlogHomeAdapter extends RecyclerView.Adapter<ItemBlogHomeAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<blogModel> feedItemList;
    private Context mContext;
    private BlogItemOnclicklinstener oNItemClickListener;

    public ItemBlogHomeAdapter(Context context, List<blogModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        settingsMain = new SettingsMain(context);

    }

    @Override
    public ItemBlogHomeAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blogs_home, null);
        return new ItemBlogHomeAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemBlogHomeAdapter.CustomViewHolder customViewHolder, int i) {
        final blogModel feedItem = feedItemList.get(i);

        customViewHolder.blog_title.setText(feedItemList.get(i).getName());
        customViewHolder.blog_category.setText(feedItemList.get(i).getCategory());
        customViewHolder.blog_date.setText(feedItemList.get(i).getDate());

        if (feedItem.isHasImage()) {
            if (!TextUtils.isEmpty(feedItem.getImage())) {
                Picasso.with(mContext).load(feedItem.getImage())
                        .resize(270, 270).centerCrop()
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(customViewHolder.imageView);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.placeholder)
                    .resize(270, 270)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oNItemClickListener.onItemClick(feedItem);
            }
        };

        customViewHolder.view.setOnClickListener(listener);

    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public BlogItemOnclicklinstener getOnItemClickListener() {
        return oNItemClickListener;
    }

    public void setOnItemClickListener(BlogItemOnclicklinstener onItemClickListener) {
        this.oNItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView blog_title, blog_category, blog_date;
        RelativeLayout view;

        CustomViewHolder(View view) {
            super(view);

            this.view = view.findViewById(R.id.linear_layout_card_view);

            this.imageView = view.findViewById(R.id.image_view);

            this.blog_title = view.findViewById(R.id.blog_title);
            this.blog_category = view.findViewById(R.id.blog_category);
            this.blog_date = view.findViewById(R.id.blog_date);
        }
    }
}
