package com.kathryn.kathryn.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.Blog.adapter.ItemBlogCommentReplyAdapter;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.ad_detail.AdRating;
import com.kathryn.kathryn.helper.BlogCommentOnclicklinstener;
import com.kathryn.kathryn.modelsList.blogCommentsModel;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class ItemRatingListAdapter extends RecyclerView.Adapter<ItemRatingListAdapter.CustomViewHolder> {

    public static List<blogCommentsModel> feedItemList;
    private Context mContext;
    private BlogCommentOnclicklinstener oNItemClickListener;
    RestService restService;
    SettingsMain settingsMain;


    public ItemRatingListAdapter(Context context, List<blogCommentsModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ItemRatingListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rating, null);

        return new ItemRatingListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemRatingListAdapter.CustomViewHolder customViewHolder, int i) {
        final blogCommentsModel feedItem = feedItemList.get(i);
        settingsMain = new SettingsMain(mContext);

        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(),
                    settingsMain.getUserPassword(), mContext);

        customViewHolder.name.setText(feedItemList.get(i).getName());
        customViewHolder.message.setText(feedItemList.get(i).getMessage());
        customViewHolder.date.setText(feedItemList.get(i).getDate());
        customViewHolder.reply.setText(feedItemList.get(i).getReply());
        customViewHolder.liketext.setText(feedItemList.get(i).getLikeReactionId());
        customViewHolder.hearttext.setText(feedItemList.get(i).getHeartReactionId());
        customViewHolder.wowtext.setText(feedItemList.get(i).getWowReactionId());
        customViewHolder.angryText.setText(feedItemList.get(i).getAngryReactionId());
        customViewHolder.ratingBar.setRating(Float.parseFloat(feedItem.getRating()));

        if (feedItemList != null && feedItemList.get(i).equals("AdRating")) {
            if (!feedItemList.get(i).getLikeReactionId().equals("")) {
                feedItemList.get(i).setClickcount(Integer.parseInt(feedItemList.get(i).getLikeReactionId()));
            } else {
                feedItemList.get(i).setClickcount(0);
            }
            if (!feedItemList.get(i).getHeartReactionId().equals("")) {
                feedItemList.get(i).setHeartclickcount(Integer.parseInt(feedItemList.get(i).getHeartReactionId()));
            } else {
                feedItemList.get(i).setHeartclickcount(0);
            }
            if (!feedItemList.get(i).getWowReactionId().equals("")) {
                feedItemList.get(i).setWOWclickcount(Integer.parseInt(feedItemList.get(i).getWowReactionId()));
            } else {
                feedItemList.get(i).setWOWclickcount(0);
            }
            if (!feedItemList.get(i).getAngryReactionId().equals("")) {
                feedItemList.get(i).setAngryclickcount(Integer.parseInt(feedItemList.get(i).getAngryReactionId()));
            } else {
                feedItemList.get(i).setAngryclickcount(0);
            }
        }

        customViewHolder.reply.setVisibility(feedItem.isCanReply() ? View.VISIBLE : View.GONE);

        if (!TextUtils.isEmpty(feedItem.getImage())) {
            Picasso.with(mContext).load(feedItem.getImage())
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

        customViewHolder.reply.setOnClickListener(listener);

        if (feedItemList.get(i).getHasReplyList()) {
            ItemBlogCommentReplyAdapter itemSendRecMesageAdapter
                    = new ItemBlogCommentReplyAdapter(mContext, feedItemList.get(i).getListitemsiner());
            if (feedItemList.get(i).getListitemsiner().size() > 0)
                customViewHolder.recyclerView.setAdapter(itemSendRecMesageAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public BlogCommentOnclicklinstener getOnItemClickListener() {
        return oNItemClickListener;
    }

    public void setOnItemClickListener(BlogCommentOnclicklinstener onItemClickListener) {
        this.oNItemClickListener = onItemClickListener;
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, message, date, reply, liketext, hearttext, wowtext, angryText;
        RatingBar ratingBar;
        RecyclerView recyclerView;
        RelativeLayout reaction;
        ImageView gifImageViewlike, gifImageViewHeart, gifImageViewWoW, gifImageViewAngry;

        CustomViewHolder(View view) {
            super(view);
            reaction = view.findViewById(R.id.reactions);

            liketext = view.findViewById(R.id.textviewLike);
            hearttext = view.findViewById(R.id.textViewHeart);
            wowtext = view.findViewById(R.id.textViewWOW);
            angryText = view.findViewById(R.id.textViewAngry);
            this.imageView = view.findViewById(R.id.image_view);
            this.name = view.findViewById(R.id.text_viewName);
            this.ratingBar = view.findViewById(R.id.ratingBar);
            this.message = view.findViewById(R.id.prices);
            this.date = view.findViewById(R.id.loginTime);
            this.reply = view.findViewById(R.id.verified);
            this.gifImageViewlike = view.findViewById(R.id.gifImageViewLike);
            this.gifImageViewHeart = view.findViewById(R.id.gifImageViewHeart);
            this.gifImageViewWoW = view.findViewById(R.id.gifImageViewWOW);
            this.gifImageViewAngry = view.findViewById(R.id.gifImageViewAngry);
            try {
                Glide.with(mContext.getApplicationContext()).load(R.drawable.like).into(gifImageViewlike);
                Glide.with(mContext.getApplicationContext()).load(R.drawable.heart).into(gifImageViewHeart);
                Glide.with(mContext.getApplicationContext()).load(R.drawable.wow).into(gifImageViewWoW);
                Glide.with(mContext.getApplicationContext()).load(R.drawable.angry).into(gifImageViewAngry);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
//            if (feedItemList.get(getAdapterPosition()).getSetReaaction()){
//                reaction.setVisibility(View.GONE);
//            }
            gifImageViewlike.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.zoomin));
                adforest_commentrating(feedItemList.get(getAdapterPosition()).getComntId(),
                        "1", liketext, getAdapterPosition());
            });
            gifImageViewHeart.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.zoomin));
                adforest_commentrating(feedItemList.get(getAdapterPosition()).getComntId()
                        , "2", hearttext, getAdapterPosition());

            });
            gifImageViewWoW.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.zoomin));
                adforest_commentrating(feedItemList.get(getAdapterPosition()).getComntId()
                        , "3", wowtext, getAdapterPosition());
            });
            gifImageViewAngry.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.zoomin));
                adforest_commentrating(feedItemList.get(getAdapterPosition()).getComntId(),
                        "4", angryText, getAdapterPosition());
            });


            LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

            this.recyclerView = view.findViewById(R.id.sublist);

            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(mContext);
            MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(MyLayoutManager);
            recyclerView.setNestedScrollingEnabled(false);

        }
    }

    public void adforest_commentrating(final String comntId, final String reactionId, TextView textView, int adapterPosition) {
        if (SettingsMain.isConnectingToInternet(mContext)) {
            JsonObject params = new JsonObject();
            params.addProperty("r_id", reactionId);
            params.addProperty("c_id", comntId);
            Log.d("info send paramas", params.toString());
            Call<ResponseBody> myCall = restService.postCommentRating(params, UrlController.AddHeaders(mContext));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info PostRating Respon", "" + responseObj.toString());
                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
//Toast.makeText(mContext, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(mContext, response.get("data").toString(), Toast.LENGTH_SHORT).show();
// FragmentAdDetail fragmentAdDetail=new FragmentAdDetail();
// Bundle bundle = new Bundle();
// bundle.putString("r_id", reactionId);
// bundle.putString("c_id", comntId);
// fragmentAdDetail.setArguments(bundle);
                                textView.setText(String.valueOf(feedItemList.get(adapterPosition).getWOWclickcount() + 1));
                                feedItemList.get(adapterPosition).setWowReactionId(
                                        String.valueOf(feedItemList.get(adapterPosition).getWOWclickcount()));


                            } else {
                                Toast.makeText(mContext, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });


        }
    }

}