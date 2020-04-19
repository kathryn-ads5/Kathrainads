package com.kathryn.kathryn.Blog.adapter;

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
import com.kathryn.kathryn.modelsList.blogCommentsModel;

public class ItemBlogCommentReplyAdapter extends RecyclerView.Adapter<ItemBlogCommentReplyAdapter.CustomViewHolder> {

    private List<blogCommentsModel> feedItemList;
    private Context mContext;

    public ItemBlogCommentReplyAdapter(Context context, List<blogCommentsModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ItemBlogCommentReplyAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comnt_list, null);
        return new ItemBlogCommentReplyAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemBlogCommentReplyAdapter.CustomViewHolder customViewHolder, int i) {
        final blogCommentsModel feedItem = feedItemList.get(i);

        customViewHolder.name.setText(feedItemList.get(i).getName());
        customViewHolder.message.setText(feedItemList.get(i).getMessage());
        customViewHolder.date.setText(feedItemList.get(i).getDate());

        if (!TextUtils.isEmpty(feedItem.getImage())) {
            Picasso.with(mContext).load(feedItem.getImage())
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
        TextView name, message, date, reply;

        CustomViewHolder(View view) {
            super(view);

            this.imageView = view.findViewById(R.id.image_view);

            this.name = view.findViewById(R.id.text_viewName);
            this.message = view.findViewById(R.id.prices);
            this.date = view.findViewById(R.id.loginTime);
            this.reply = view.findViewById(R.id.verified);

            reply.setVisibility(View.GONE);
        }
    }
}
